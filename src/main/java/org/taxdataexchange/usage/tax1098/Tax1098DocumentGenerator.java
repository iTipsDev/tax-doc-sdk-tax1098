package org.taxdataexchange.usage.tax1098;

import irs.fdxtax640.jsonserializers.Tax1098Serializer;
import irs.fdxtax640.jsonserializers.TaxStatementListSerializer;
import irs.fdxtax640.map2objects.Map2Tax1098;
import irs.fdxtax640.obj2mappers.TaxStatementList2Map;

import org.apache.commons.lang3.time.StopWatch;
import org.taxdataexchange.core.pdfbuilders.Tax1098PdfBuilder;

import org.taxdataexchange.core.utils.*;
import org.taxdataexchange.core.csv.GenericCsvMapReader;

import org.openapitools.client.model.*;

import returnagnosticutils.BytesToFile;
import returnagnosticutils.FileToString;
import returnagnosticutils.Jsonizer;
import returnagnosticutils.StringToFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Read CSV file for one company and generate 1099-MISC PDFs

public class Tax1098DocumentGenerator {

    public static final String INPUT_DIRECTORY = "input";

    public static final String OUTPUT_DIRECTORY = "output";

    private void processOneRow(
        String companyName,
        int rowNumber,
        Map<String, String> row
    ) {

        String outputDir = String.format( "%s/%s/%06d", OUTPUT_DIRECTORY, companyName, rowNumber );

        // Save input
        {
            String asJson = Jsonizer.toJson( row );
            String outputFile = String.format( "%06d.map.json", rowNumber );
            StringToFile.writeToDirFile( asJson, outputDir, outputFile );
        }

        // Convert to object and save
        Tax1098 obj = Map2Tax1098.generate( row );
        {
            String asJson = Tax1098Serializer.serialize( obj );
            String outputFile = String.format( "%06d.obj.json", rowNumber );
            StringToFile.writeToDirFile( asJson, outputDir, outputFile );
        }

        // Create TaxStatementList object
        TaxData taxData = new TaxData( );
        taxData.setTax1098( obj );
        TaxStatement taxStatement = new TaxStatement( );
        taxStatement.addFormsItem( taxData );
        TaxStatementList taxStatementList = new TaxStatementList( );
        taxStatementList.addStatementsItem( taxStatement );
        {
            String asJson = TaxStatementListSerializer.serialize( taxStatementList );
            String outputFile = String.format( "%06d.tax-statement-list.json", rowNumber );
            StringToFile.writeToDirFile( asJson, outputDir, outputFile );
        }

        // Generate PDFs
        Tax1098PdfBuilder pdfBuilder = new Tax1098PdfBuilder( );
        StringBuilder stringBuilder = new StringBuilder();
        StopWatch stopWatch = new StopWatch( );
        stopWatch.start( );
        pdfBuilder.build3( stringBuilder, stopWatch, taxStatementList );
        {
            // Issuer copy
            String fileName = String.format( "%06d.issuer.pdf", rowNumber );
            byte[] pdfBytes = pdfBuilder.getIssuerPdfBytes( );
            BytesToFile.writeToDirFile(
                pdfBytes,
                outputDir,
                fileName
            );
        }

        {
            // Print and mail copy
            byte[] pdfBytes = pdfBuilder.getPrintPdfBytes( );
            pdfBytes = PdfWatermarker.addWatermarkToPdf( pdfBytes, "Sample" );
            String fileName = String.format( "%06d.print.pdf", rowNumber );
            BytesToFile.writeToDirFile(
                pdfBytes,
                outputDir,
                fileName
            );

            byte[] pngBytes = Pdf2PngConverter.convertBytes( pdfBytes );
            String pngFileName = String.format( "%06d.print.png", rowNumber );
            BytesToFile.writeToDirFile(
                pngBytes,
                outputDir,
                pngFileName
            );
        }

        {
            // Email or download copy
            byte[] pdfBytes = pdfBuilder.getDownloadPdfBytes( );
            pdfBytes = PdfWatermarker.addWatermarkToPdf( pdfBytes, "Sample" );
            String fileName = String.format( "%06d.download.pdf", rowNumber );
            BytesToFile.writeToDirFile(
                pdfBytes,
                outputDir,
                fileName
            );

            byte[] pngBytes = Pdf2PngConverter.convertBytes( pdfBytes );
            String pngFileName = String.format( "%06d.download.png", rowNumber );
            BytesToFile.writeToDirFile(
                pngBytes,
                outputDir,
                pngFileName
            );

        }

    }

    private void processCsvForCompany(
        String companyName
    ) {

        GenericCsvMapReader csvReader = new GenericCsvMapReader( );

        // CSV file
        String csvFileName  = "Tax1098.csv";

        // CSV dir
        String csvDirName = String.format( "%s/%s", INPUT_DIRECTORY, companyName );

        // Read content
        String csvContent = FileUtils.readDirFile(
            csvDirName,
            csvFileName
        );

        // Convert to list of maps
        List<Map<String, String>> rows = csvReader.readStringWithCsvMapReader(
            csvContent
        );

        // Save
        String outputDirName = String.format( "%s/%s", OUTPUT_DIRECTORY, companyName );
        String outputFileName = "Tax1098.rows.json";
        String asJson = Jsonizer.toJson( rows );
        StringToFile.writeToDirFile( asJson, outputDirName, outputFileName );

        // Process each row
        int rowNumber = 0;
        for ( Map<String, String> row : rows ) {

            rowNumber++;
            processOneRow( companyName, rowNumber, row );

        }

    }

    public static void main(String[] args) {

        System.out.println( "Tax1098DocumentGenerator Begin" );

        String companyName = "company1";

        Tax1098DocumentGenerator generator = new Tax1098DocumentGenerator( );

        generator.processCsvForCompany( companyName );

        System.out.println( "Tax1098DocumentGenerator Done" );

    }

}
