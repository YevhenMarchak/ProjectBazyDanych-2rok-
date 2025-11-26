package model;

public class receipts {
    private int receipt_id;
    private int supplier_id;
    private int employee_id;
    private int receipt_date;
    private int external_invoice_no;
    private int status;
    public receipts(int receipt_id, int supplier_id, int employee_id, int receipt_date, int external_invoice_no, int status)
    {
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

    public int getEmployee_id() {
        return employee_id;
    }

    public int getReceipt_date() {
        return receipt_date;
    }

    public int getExternal_invoice_no() {
        return external_invoice_no;
    }

    public int getStatus() {
        return status;
    }
}
//koniec klasy