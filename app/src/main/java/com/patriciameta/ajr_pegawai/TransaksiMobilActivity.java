package com.patriciameta.ajr_pegawai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.patriciameta.ajr_pegawai.adapters.LapMobilAdapter;
import com.patriciameta.ajr_pegawai.api.ApiClient;
import com.patriciameta.ajr_pegawai.api.ApiInterface;
import com.patriciameta.ajr_pegawai.models.Transaksi;
import com.patriciameta.ajr_pegawai.models.TransaksiResponse;
import com.patriciameta.ajr_pegawai.preferences.UserPreferences;
import com.patriciameta.ajr_pegawai.users.User;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransaksiMobilActivity extends AppCompatActivity {
    public static Activity activity;

    public static Activity transaksiActivity;
    private UserPreferences userPreferences;
    private Context context;
    private Transaksi transaksi;
    private User user;
    private ArrayList<Transaksi> listTransaksi;

    private ApiInterface apiService;
    private SwipeRefreshLayout srTransaksi;
    private LapMobilAdapter adapter;
    private SearchView svTransaksi;
    private LinearLayout layoutLoading;
    private FloatingActionButton fab_pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transaksiActivity = this;
        setContentView(R.layout.activity_transaksi_mobil);

        //  Ubah Title pada App Bar Aplikasi
        setTitle("Laporan Pendapatan Mobil");
        userPreferences = new UserPreferences(TransaksiMobilActivity.this);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        layoutLoading = findViewById(R.id.layout_loading);
        srTransaksi = findViewById(R.id.sr_transaksi);
        svTransaksi = findViewById(R.id.sv_transaksi);
        fab_pdf = findViewById(R.id.fab_pdf);

        srTransaksi.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllTransaksi();
            }
        });
        svTransaksi.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        fab_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cetakPdf();
                } catch (FileNotFoundException | DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        RecyclerView rvTransaksi = findViewById(R.id.rv_transaksi);
        adapter = new LapMobilAdapter(new ArrayList<>(), this);
        rvTransaksi.setLayoutManager(new LinearLayoutManager(TransaksiMobilActivity.this,
                LinearLayoutManager.VERTICAL, false));
        rvTransaksi.setAdapter(adapter);

        getAllTransaksi();
    }

    private void getAllTransaksi(){
        Call<TransaksiResponse> call = apiService.getIncomeMobil();
        srTransaksi.setRefreshing(true);
        call.enqueue(new Callback<TransaksiResponse>() {

            @Override
            public void onResponse(Call<TransaksiResponse> call, Response<TransaksiResponse> response) {
                if (response.isSuccessful()) {
                    listTransaksi = new ArrayList<>();
                    for (int i=0; i<response.body().getTransaksiList().size();i++) {
                        listTransaksi.add(response.body().getTransaksiList().get(i));
                    }
                    adapter.setTransaksiList(response.body().getTransaksiList());
                    adapter.getFilter().filter(svTransaksi.getQuery());
                }else {
                    try {
                        JSONObject jObjError = new
                                JSONObject(response.errorBody().string());
                        Toast.makeText(TransaksiMobilActivity.this,
                                jObjError.getString("message"),
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(TransaksiMobilActivity.this,
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                srTransaksi.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<TransaksiResponse> call, Throwable t) {
                Toast.makeText(TransaksiMobilActivity.this, "Network error",
                        Toast.LENGTH_SHORT).show();
                srTransaksi.setRefreshing(false);
            }
        });
    }

    // Fungsi untuk menampilkan layout loading
    private void setLoading(boolean isLoading) {
        if (isLoading) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            layoutLoading.setVisibility(View.VISIBLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            layoutLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_nav,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_home) {
//            TransaksiMobilActivity.activity.finish();
            finishAndRemoveTask();
            startActivity(new Intent(TransaksiMobilActivity.this, MainActivity.class));
        } else if (item.getItemId() == R.id.menu_mobil) {
            //
        } else if (item.getItemId() == R.id.menu_cust) {
//            TransaksiMobilActivity.activity.finish();
            finishAndRemoveTask();
            startActivity(new Intent(TransaksiMobilActivity.this, BulanActivity2.class));
        } else if (item.getItemId() == R.id.menu_topDriver) {
//            TransaksiMobilActivity.activity.finish();
            finishAndRemoveTask();
            startActivity(new Intent(TransaksiMobilActivity.this, BulanActivity3.class));
        } else if (item.getItemId() == R.id.menu_topCust) {
//            TransaksiMobilActivity.activity.finish();
            finishAndRemoveTask();
            startActivity(new Intent(TransaksiMobilActivity.this, BulanActivity4.class));
        } else if (item.getItemId() == R.id.menu_logout){
            // Jika menu yang dipilih adalah menu Exit, maka tampilkan sebuah dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_confirm).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    userPreferences.logout();
                    checkLogin();
                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkLogin(){
        if(!userPreferences.checkLogin()){
            startActivity(new Intent(TransaksiMobilActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void cetakPdf() throws FileNotFoundException, DocumentException {
        File folder = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        if (!folder.exists()) {
            folder.mkdir();
        }

        Date currentTime = Calendar.getInstance().getTime();
        String pdfName = currentTime.getTime() + ".pdf";

        File pdfFile = new File(folder.getAbsolutePath(), pdfName);
        OutputStream outputStream = new FileOutputStream(pdfFile);

        com.itextpdf.text.Document document = new
                com.itextpdf.text.Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // bagian header
        Paragraph judul = new Paragraph("Laporan Pendapatan Mobil \n\n",
                new com.itextpdf.text.Font(Font.FontFamily.TIMES_ROMAN, 16,
                        Font.BOLD, BaseColor.BLACK));

        judul.setAlignment(Element.ALIGN_CENTER);
        document.add(judul);

        // Buat tabel
        PdfPTable tables = new PdfPTable(new float[]{16, 8});

        // Settingan ukuran tabel
        tables.getDefaultCell().setFixedHeight(50);
        tables.setTotalWidth(PageSize.A4.getWidth());
        tables.setWidthPercentage(100);
        tables.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell cellSupplier = new PdfPCell();
        cellSupplier.setPaddingLeft(20);
        cellSupplier.setPaddingBottom(10);
        cellSupplier.setBorder(Rectangle.NO_BORDER);

        userPreferences = new UserPreferences(transaksiActivity);
        user = userPreferences.getUserLogin();

        Paragraph kepada = new Paragraph(
                "Kepada Yth: \n" + user.getNama() + "\n",
                new com.itextpdf.text.Font(Font.FontFamily.TIMES_ROMAN, 10,
                        Font.NORMAL, BaseColor.BLACK));
        cellSupplier.addElement(kepada);
        tables.addCell(cellSupplier);

        Paragraph NomorTanggal = new Paragraph(
                "No : " + "123456789" + "\n\n" +
                        "Tanggal : " + new SimpleDateFormat("dd/MM/yyyy",
                        Locale.getDefault()).format(currentTime) + "\n",
                new
                        com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,
                        com.itextpdf.text.Font.NORMAL, BaseColor.BLACK));

        NomorTanggal.setPaddingTop(5);
        tables.addCell(NomorTanggal);
        document.add(tables);
        com.itextpdf.text.Font f = new
                com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,
                com.itextpdf.text.Font.NORMAL, BaseColor.BLACK);

        Paragraph Pembuka = new Paragraph("\nBerikut merupakan laporan: \n\n", f);
        Pembuka.setIndentationLeft(20);
        document.add(Pembuka);
        PdfPTable tableHeader = new PdfPTable(new float[]{5, 5, 5, 5});

        tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableHeader.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        tableHeader.getDefaultCell().setFixedHeight(30);
        tableHeader.setTotalWidth(PageSize.A4.getWidth());
        tableHeader.setWidthPercentage(100);

        // Setup Column
        PdfPCell h1 = new PdfPCell(new Phrase("Nama Mobil"));
        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
        h1.setPaddingBottom(5);
        PdfPCell h2 = new PdfPCell(new Phrase("Tipe Mobil"));
        h2.setHorizontalAlignment(Element.ALIGN_CENTER);
        h2.setPaddingBottom(5);
        PdfPCell h3 = new PdfPCell(new Phrase("Total Transaksi"));
        h3.setHorizontalAlignment(Element.ALIGN_CENTER);
        h3.setPaddingBottom(5);
        PdfPCell h4 = new PdfPCell(new Phrase("Jumlah Pendapatan"));
        h4.setHorizontalAlignment(Element.ALIGN_CENTER);
        h4.setPaddingBottom(5);

        tableHeader.addCell(h1);
        tableHeader.addCell(h2);
        tableHeader.addCell(h3);
        tableHeader.addCell(h4);

        // Beri warna untuk kolom table
        for (PdfPCell cells : tableHeader.getRow(0).getCells()) {
            cells.setBackgroundColor(BaseColor.PINK);
        }

        document.add(tableHeader);
        PdfPTable tableData = new PdfPTable(new float[]{5, 5, 5, 5});

        tableData.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        tableData.getDefaultCell().setFixedHeight(30);
        tableData.setTotalWidth(PageSize.A4.getWidth());
        tableData.setWidthPercentage(100);
        tableData.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        // masukan data jadwal jadi baris
        int i = 1;
        for (Transaksi P : listTransaksi) {
            i = i+1;
            tableData.addCell(P.getNama_aset());
            tableData.addCell(P.getTipe_aset());
            tableData.addCell(P.getJumlah().toString());
            tableData.addCell(P.getPendapatan().toString());
        }

        document.add(tableData);

        com.itextpdf.text.Font h = new
                com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,
                com.itextpdf.text.Font.NORMAL);

        String tglDicetak = currentTime.toLocaleString();
        Paragraph P = new Paragraph("\nDicetak tanggal " + tglDicetak, h);
        P.setAlignment(Element.ALIGN_RIGHT);
        document.add(P);
        document.close();
        previewPdf(pdfFile);
        Toast.makeText(this, "PDF berhasil dibuat", Toast.LENGTH_SHORT).show();
    }

    private void previewPdf(File pdfFile) {
        PackageManager packageManager = this.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(testIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);

        if (list.size() > 0) {
            Uri uri;
            uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(uri, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            this.grantUriPermission(this.getPackageName(), uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(pdfIntent);
        }
    }
}