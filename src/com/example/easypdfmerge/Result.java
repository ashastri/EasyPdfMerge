package com.example.easypdfmerge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;


public class Result extends Fragment {

    View mListView;
    private List<File> fileNameList;
    private File file;
    private File[] filelist1 = null;
    File[] fileArr = null;
    List<File> flLst = null;
    String[] pdflist;
	GridView gridview;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.edit, container,false);
		
        mListView = view.findViewById(R.id.listView3);
        file = new File("/storage/sdcard0/MergedFiles/");
        fileNameList = getFileListfromSDCard();
        pdflist = new String[fileNameList.size()];
        for(int i = 0;i<fileNameList.size();i++)
        {
            pdflist[i] = fileNameList.get(i).getName();
       }
        ArrayAdapter<String> arrayAdapterList1 = new ArrayAdapter<String>(getActivity(),
        		android.R.layout.simple_list_item_1, pdflist);
        ((ListView) mListView).setAdapter(arrayAdapterList1);
        ((ListView) mListView).setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ((ListView) mListView).setOnItemClickListener(new AdapterView.OnItemClickListener(
        		) {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						PackageManager packageManager =   getActivity().getPackageManager();
				        Intent testIntent = new Intent(Intent.ACTION_VIEW);
				        testIntent.setType("application/pdf");
				        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
				        if (list.size() > 0) { //&& f1[(int) id].isFile()) {
				            Intent intent = new Intent();
				            intent.setAction(Intent.ACTION_VIEW);
				            File pdffile = new File(flLst.get((int) id).toString());
				            Uri uri = Uri.fromFile(pdffile);
				            intent.setDataAndType(uri, "application/pdf");
				            startActivity(intent);
				        }
						
					}
		});
        return view;
    }

	private  List<File> getFileListfromSDCard() {
        String state = Environment.getExternalStorageState();
        flLst = new ArrayList<File>();
        if (Environment.MEDIA_MOUNTED.equals(state) && file.isDirectory()) {
            File[] fileArr = file.listFiles();
            int length = fileArr.length;
            for (int i = 0; i < length; i++) {
                File f = fileArr[i];
                if(f.isDirectory()) {
                 filelist1 = f.listFiles();
                    if(filelist1.length > 0) {
                    for(int j=0; j< filelist1.length ; j++) {
                        File f1 = filelist1[j];
                     //   if(f1.getName().contains(".pdf"))
         //              flLst.add(f1.getAbsolutePath());
                        flLst.add(f1);
                    }
                    }
                }
                flLst.add(f);
            }
        }

        return flLst;
    }

	
    protected void onListItemClick(ListView l, View v, int position, long id) {
        
        List<InputStream> pdfs = new ArrayList<InputStream>();
        try {
			pdfs.add(new FileInputStream("/storage/sdcard0/data/AShastri_Resume.pdf"));
	        pdfs.add(new FileInputStream("/storage/sdcard0/Download/Travel_Interary.pdf"));
	        OutputStream output = new FileOutputStream("/storage/sdcard0/Download/testpdf.pdf");
			Merge.concatPDFs(pdfs, output, true);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
}


	
