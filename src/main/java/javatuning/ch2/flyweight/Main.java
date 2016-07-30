package javatuning.ch2.flyweight;

public class Main {

    public static void main(String[] args) {
        ReportManagerFactory rmf = new ReportManagerFactory();
        IReportManager rm = rmf.getFinancialReportManager("A");
        System.out.println(rm.createReport());
    }

}
