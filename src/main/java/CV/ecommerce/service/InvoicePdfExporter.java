package CV.ecommerce.service;

import CV.ecommerce.dto.response.InvoiceResponse;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;

@Component
public class InvoicePdfExporter {

    @SneakyThrows
    public byte[] export(InvoiceResponse invoice) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLUE);
        Paragraph title = new Paragraph("Invoice", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        addRow(table, "Invoice Code:", invoice.getInvoiceCode());
        addRow(table, "Order ID:", invoice.getOrderId());
        addRow(table, "Customer name:", invoice.getBillingName());
        addRow(table, "Email:", invoice.getBillingEmail());
        addRow(table, "Phone:", invoice.getBillingPhone());
        addRow(table, "Shipping address:", invoice.getShippingAddress());
        addRow(table, "Payment method:", invoice.getPaymentMethod());
        addRow(table, "Payment status:", invoice.getPaymentStatus());
        addRow(table, "Total amount:", String.valueOf(invoice.getPaymentAmount()));
        addRow(table, "Payment date:", invoice.getPaymentDate().toString());

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    private void addRow(PdfPTable table, String key, String value) {
        table.addCell(new Phrase(key));
        table.addCell(new Phrase(value == null ? "" : value));
    }
}
