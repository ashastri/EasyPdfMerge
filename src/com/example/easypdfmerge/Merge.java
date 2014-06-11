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
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class Merge extends Fragment implements OnClickListener {

	private HashMap<Integer, Boolean> mIsChecked = new HashMap<Integer, Boolean>();
	private List<File> fileNameList;
	private File file;
	private File[] filelist1 = null;
	int output1 = 0;
	static ProgressDialog progressBar;
	View mListView;
	File[] fileArr = null;
	List<File> flLst = null;
	String[] pdflist;
	List<File> selectedFiles = new ArrayList<File>();
	CheckedTextView ct1 = null;
	EditText eTextView;
	String fileName = "";
	List<InputStream> filesToMerge = null;
	OutputStream output = null;
	List<String> pos = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.merge, container, false);
		mListView = view.findViewById(R.id.listView1);
		file = new File("/storage/sdcard0");
		fileNameList = getFileListfromSDCard();
		pdflist = new String[fileNameList.size()];
		for (int i = 0; i < fileNameList.size(); i++) {
			pdflist[i] = fileNameList.get(i).getName().toUpperCase();
		}
		final ArrayAdapter<String> arrayAdapterList1 = new ArrayAdapter<String>(
				getActivity(),
				android.R.layout.simple_list_item_multiple_choice, pdflist);
		((ListView) mListView).setAdapter(arrayAdapterList1);
		((ListView) mListView).setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		((ListView) mListView)
				.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						pos.add(String.valueOf(arg2));
						selectedFiles.add(flLst.get(arg2));
						CheckedTextView ctv = (CheckedTextView) arg1;
						Toast.makeText(getActivity(),
								ctv.getText().toString() + " : " + arg2,
								Toast.LENGTH_SHORT).show();
					}
				});
		ct1 = (CheckedTextView) view.findViewById(android.R.id.text1);
		Button merge = (Button) view.findViewById(R.id.footer);
		merge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				LayoutInflater inflater = getActivity().getLayoutInflater();
				final View v1 = inflater.inflate(R.layout.dialog, null);
				builder.setView(v1)
						.setTitle("Save File As ")
						.setCancelable(true)
						.setPositiveButton("Save",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										eTextView = (EditText) v1
												.findViewById(R.id.filePath);
										fileName = eTextView.getText()
												.toString();
										// Get List of files to Merge
										for (int j = 0; j < selectedFiles
												.size(); j++) {
											try {
												if (filesToMerge == null)
													filesToMerge = new ArrayList<InputStream>();
												filesToMerge
														.add(new FileInputStream(
																selectedFiles
																		.get(j)));
											} catch (FileNotFoundException e) {
												e.printStackTrace();
											}
										}
										// Default file path
										try {
											output = new FileOutputStream(
													"/storage/sdcard0/MergedFiles/"
															+ fileName);
										} catch (FileNotFoundException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										progressBar = new ProgressDialog(
												getActivity());
										progressBar.setCancelable(true);
										progressBar
												.setMessage("Saving File ...");
										progressBar
												.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
										progressBar.setProgress(0);
										progressBar.setMax(100);
										progressBar.show();

										new Thread(new Runnable() {

											@Override
											public void run() {
												try {
													output1 = Merge.concatPDFs(
															filesToMerge,
															output, true);
												} finally {
													ListView l = (ListView) mListView;
													for (int i = 0; i < l
															.getChildCount(); i++) {
														View childView = l
																.getChildAt(i);

														if (childView != null
																&& childView instanceof CheckedTextView) {
															// CheckedTextView
															// cv =
															// (CheckedTextView)
															// childView;
															// cv.setChecked(false);
															// cv.setCheckMarkDrawable(android.R.id.text1);
														}

													}

													progressBar.dismiss();

												}
											}
										}).start();

										if (output1 == 0) {

											ct1 = (CheckedTextView) view
													.findViewById(android.R.id.text1);
											ct1.setChecked(false);
											Toast.makeText(getActivity(),
													"File Saved",
													Toast.LENGTH_LONG).show();
											((MainActivity) getActivity())
													.setCurrentItem(2, true);
										}
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								}).show();
			}
		});
		return view;
	}

	// Get list of pdf files from internal storage
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

	// Open pdf file for viewing
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// super.onListItemClick(l, v, position, id);
		PackageManager packageManager = getActivity().getPackageManager();
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		testIntent.setType("application/pdf");
		List list = packageManager.queryIntentActivities(testIntent,
				PackageManager.MATCH_DEFAULT_ONLY);
		if (list.size() > 0) { // && f1[(int) id].isFile()) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			File pdffile = new File(flLst.get((int) id).toString());
			Uri uri = Uri.fromFile(pdffile);
			intent.setDataAndType(uri, "application/pdf");
			startActivity(intent);
		}
	}

	public static int concatPDFs(List<InputStream> streamOfPDFFiles,
			OutputStream outputStream, boolean paginate) {
		int success = 0;
		Document document = new Document();
		try {
			List<InputStream> pdfs = streamOfPDFFiles;
			List<PdfReader> readers = new ArrayList<PdfReader>();
			int totalPages = 0;
			Iterator<InputStream> iteratorPDFs = pdfs.iterator();

			// Create Readers for the pdfs.
			while (iteratorPDFs.hasNext()) {
				InputStream pdf = iteratorPDFs.next();
				PdfReader pdfReader = new PdfReader(pdf);
				progressBar.setProgress(25);
				PdfReader.unethicalreading = true;
				readers.add(pdfReader);
				totalPages += pdfReader.getNumberOfPages();
			}
			// Create a writer for the outputstream
			PdfWriter writer = PdfWriter.getInstance(document, outputStream);

			document.open();
			BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
					BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			PdfContentByte cb = writer.getDirectContent(); // Holds the PDF
			// data

			PdfImportedPage page;
			int currentPageNumber = 0;
			int pageOfCurrentReaderPDF = 0;
			Iterator<PdfReader> iteratorPDFReader = readers.iterator();

			// Loop through the PDF files and add to the output.
			while (iteratorPDFReader.hasNext()) {
				PdfReader pdfReader = iteratorPDFReader.next();

				// Create a new page in the target for each source page.
				while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
					document.newPage();
					pageOfCurrentReaderPDF++;
					currentPageNumber++;
					page = writer.getImportedPage(pdfReader,
							pageOfCurrentReaderPDF);
					cb.addTemplate(page, 0, 0);
					progressBar.setProgress(50);
					// Code for pagination.
					if (paginate) {
						cb.beginText();
						cb.setFontAndSize(bf, 9);
						cb.showTextAligned(PdfContentByte.ALIGN_CENTER, ""
								+ currentPageNumber + " of " + totalPages, 520,
								5, 0);
						cb.endText();
					}
				}
				pageOfCurrentReaderPDF = 0;
			}
			progressBar.setProgress(100);
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

			// outputStream.close();
			catch (Exception e) {
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
		}
		return success;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.footer) {
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
