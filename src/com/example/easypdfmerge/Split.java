package com.example.easypdfmerge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class Split extends Fragment implements OnClickListener {

	private HashMap<Integer, Boolean> mIsChecked = new HashMap<Integer, Boolean>();
	View mListView;
	private List<File> fileNameList;
	private File file;
	private File[] filelist1 = null;
	File[] fileArr = null;
	List<File> flLst = null;
	String[] pdflist;
	List<File> selectedFiles = new ArrayList<File>();
	CheckedTextView ct1 = null;
	EditText eTextView;
	String fileName = "";
	EditText fromPage = null;
	EditText toPage = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.split, container, false);
		mListView = view.findViewById(R.id.listView2);
		file = new File("/storage/sdcard0");
		fileNameList = getFileListfromSDCard();
		pdflist = new String[fileNameList.size()];
		for (int i = 0; i < fileNameList.size(); i++) {
			pdflist[i] = fileNameList.get(i).getName().toUpperCase();
		}
		ArrayAdapter<String> arrayAdapterList1 = new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_multiple_choice, pdflist);
		((ListView) mListView).setAdapter(arrayAdapterList1);
		((ListView) mListView).setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		((ListView) mListView)
				.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						selectedFiles.add(flLst.get(arg2));
						CheckedTextView ctv = (CheckedTextView) arg1;
						Toast.makeText(getActivity(),
								ctv.getText().toString() + " : " + arg2,
								Toast.LENGTH_SHORT).show();
						// do your stuff in here!
					}
				});
		Button merge = (Button) view.findViewById(R.id.splitfooter);
		merge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			//	Dialog dialog;
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				LayoutInflater inflater = getActivity().getLayoutInflater();
				final View v1 = inflater.inflate(R.layout.split_dialog, null);
				builder.setView(v1)
				.setTitle("Save File As ").
				setCancelable(true)
				.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                	fromPage = (EditText)v1.findViewById(R.id.from_page_number);
	                	toPage = (EditText)v1.findViewById(R.id.to_page_number);
	                	eTextView = (EditText)v1.findViewById(R.id.split_filePath);
	                	fileName = eTextView.getText().toString();
	                	int output1 = 0;
	    				InputStream filesToMerge = null;
	    				for (int j = 0; j < selectedFiles.size(); j++) {
	    					try {
	    						if (filesToMerge == null)
	    							filesToMerge = new FileInputStream(selectedFiles.get(j));
	    					} catch (FileNotFoundException e) {
	    						e.printStackTrace();
	    					}
	    				}
	    				
	    				OutputStream output = null;
	    				try {
	    					output = new FileOutputStream("/storage/sdcard0/MergedFiles/"+ fileName);
	    				} catch (FileNotFoundException e) {
	    					e.printStackTrace();
	    				}
	    				int fromPageNumber = Integer.parseInt(fromPage.getText().toString());
	    				int toPageNumber = Integer.parseInt(toPage.getText().toString());
	    				output1 = Split.splitPDF(filesToMerge, output,fromPageNumber ,toPageNumber );
	    				if(output1 == 0) {
	    					Toast.makeText(getActivity(), "File Saved", Toast.LENGTH_LONG).show();
	    				}
	                }
	            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).show();
			}
		});
		return view;
	}
	private List<File> getFileListfromSDCard() {
		String state = Environment.getExternalStorageState();
		flLst = new ArrayList<File>();
		if (Environment.MEDIA_MOUNTED.equals(state) && file.isDirectory()) {
			File[] fileArr = file.listFiles();
			int length = fileArr.length;
			for (int i = 0; i < length; i++) {
				File f = fileArr[i];
				if (f.isDirectory()) {
					filelist1 = f.listFiles();
					if (filelist1.length > 0) {
						for (int j = 0; j < filelist1.length; j++) {
							File f1 = filelist1[j];
							if (f1.getName().contains(".pdf"))
								// flLst.add(f1.getAbsolutePath());
								flLst.add(f1);
						}
					}
				}
			}
		}

		return flLst;
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		// super.onListItemClick(l, v, position, id);
		PackageManager packageManager = getActivity().getPackageManager();
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		testIntent.setType("application/pdf");
		List list = packageManager.queryIntentActivities(testIntent,
				PackageManager.MATCH_DEFAULT_ONLY);
		if (list.size() > 0) { 
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			File pdffile = new File(flLst.get((int) id).toString());
			Uri uri = Uri.fromFile(pdffile);
			intent.setDataAndType(uri, "application/pdf");
			startActivity(intent);
		}
	}

	public static int splitPDF(InputStream inputStream,
	        OutputStream outputStream, int fromPage, int toPage) {
		int succ = 0;
	    Document document = new Document();
	    try {
	        PdfReader inputPDF = new PdfReader(inputStream);
	 
	        int totalPages = inputPDF.getNumberOfPages();
	 
	        //make fromPage equals to toPage if it is greater
	        if(fromPage > toPage ) {
	            fromPage = toPage;
	        }
	        if(toPage > totalPages) {
	            toPage = totalPages;
	        }
	 
	 
	        // Create a writer for the outputstream
	        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
	 
	        document.open();
	        PdfContentByte cb = writer.getDirectContent(); // Holds the PDF data
	        PdfImportedPage page;
	 
	        while(fromPage <= toPage) {
	            document.newPage();
	            page = writer.getImportedPage(inputPDF, fromPage);
	            cb.addTemplate(page, 0, 0);
	            fromPage++;
	        }
	        outputStream.flush();
	        document.close();
	        outputStream.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (document.isOpen())
	            document.close();
	        try {
	            if (outputStream != null)
	                outputStream.close();
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        }
	    }
	    return succ;
	}
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.splitfooter) {
			List<InputStream> pdfs = new ArrayList<InputStream>();
			for (int i = 0; i < flLst.size(); i++) {
				if (mIsChecked.get(i) != null) {
					if (mIsChecked.get(i)) {
						try {
							pdfs.add(new FileInputStream(flLst.get((int) i)
									.toString()));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}

				}
			}
		}

	}
}
