package model;

import java.time.LocalDate;

public class receipts {

    private int receipt_id;
    private int supplier_id;
    private Integer employee_id;
    private LocalDate receipt_date;
    private String external_invoice_no;
    private String status;

    public receipts(int receipt_id,
                    int supplier_id,
                    Integer employee_id,
                    LocalDate receipt_date,
                    String external_invoice_no,
                    String status) {

        this.receipt_id = receipt_id;
        this.supplier_id = supplier_id;
        this.employee_id = employee_id;
        this.receipt_date = receipt_date;
        this.external_invoice_no = external_invoice_no;
        this.status = status;
    }

    public int getReceipt_id() {
        return receipt_id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public Integer getEmployee_id() {
        return employee_id;
    }

    public LocalDate getReceipt_date() {
        return receipt_date;
    }

    public String getExternal_invoice_no() {
        return external_invoice_no;
    }

    public String getStatus() {
        return status;
    }

    public void setReceipt_id(int receipt_id) {
        this.receipt_id = receipt_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public void setEmployee_id(Integer employee_id) {
        this.employee_id = employee_id;
    }

    public void setReceipt_date(LocalDate receipt_date) {
        this.receipt_date = receipt_date;
    }

    public void setExternal_invoice_no(String external_invoice_no) {
        this.external_invoice_no = external_invoice_no;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
