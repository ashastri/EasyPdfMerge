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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;


public class CopyOfMerge extends Fragment implements OnClickListener {


	private HashMap<Integer, CheckedTextView> mCheckedList =new HashMap<Integer, CheckedTextView>();
	private HashMap<Integer, Boolean> mIsChecked =new HashMap<Integer, Boolean>();
    View mListView;
    private List<File> fileNameList;
//    private FlAdapter mAdapter;
    private File file;
  //  ArrayAdapter<String> adapter;
    private File[] filelist1 = null;
    File[] fileArr = null;
    List<File> flLst = null;
    String[] pdflist;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
//		return inflater.inflate(R.layout.merge, container, false);

		View view = inflater.inflate(R.layout.merge, container,false);
        mListView = view.findViewById(R.id.listView1);
        CheckedTextView ct = (CheckedTextView) view.findViewById(R.id.row1);
        Resources myResources = getResources();

        Typeface tf = Typeface.createFromAsset(myResources.getAssets(),"fontstyle.ttf");
        file = new File("/storage/sdcard0");
        fileNameList = getFileListfromSDCard();
        pdflist = new String[fileNameList.size()];
        for(int i = 0;i<fileNameList.size();i++)
        {
            pdflist[i] = fileNameList.get(i).getName().toUpperCase();
       }
        Button btn = (Button) view.findViewById(R.id.footer);
        ArrayAdapter<String> arrayAdapterList1 = new ArrayAdapter<String>(getActivity(),
        		R.layout.checkbox, pdflist);
        ((ListView) mListView).setAdapter(arrayAdapterList1);
        ((ListView) mListView).setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
                        if(f1.getName().contains(".pdf"))
         //              flLst.add(f1.getAbsolutePath());
                        flLst.add(f1);
                    }
                    }
                }
            }
        }

        return flLst;
    }
	

    protected void onListItemClick(ListView l, View v, int position, long id) {
   //     super.onListItemClick(l, v, position, id);
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
        List<InputStream> pdfs = new ArrayList<InputStream>();
        try {
			pdfs.add(new FileInputStream("/storage/sdcard0/data/AShastri_Resume.pdf"));
	        pdfs.add(new FileInputStream("/storage/sdcard0/Download/Travel_Interary.pdf"));
	        OutputStream output = new FileOutputStream("/storage/sdcard0/Download/testpdf.pdf");
			CopyOfMerge.concatPDFs(pdfs, output, true);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
    
    public static void concatPDFs(List<InputStream> streamOfPDFFiles,
            OutputStream outputStream, boolean paginate) {
 
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
                    page = writer.getImportedPage(pdfReader,pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);
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
        
    
	           //      outputStream.close();
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
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.footer) {
			List<InputStream> pdfs = new ArrayList<InputStream>();
			for(int i = 0 ; i<flLst.size();i++) {
				if(mIsChecked.get(i) != null) {
					if(mIsChecked.get(i)) {
						try {
							pdfs.add(new FileInputStream(flLst.get((int) i).toString()));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
						
				}
			}
		}
		
	}}


	
