/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author ASUS
 */
public class RupiahGenerator {

    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

    public RupiahGenerator() {

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);

    }

    public double getNumber(String rupiah) {
        double nilai = 0;
        try {
            Number number = kursIndonesia.parse(rupiah);
            nilai = number.doubleValue();
        } catch (Exception ex) {
            System.out.println("Error in Rupiah Generator");
        }

        return nilai;
    }
    
   public int getIntNumber(String rupiah){
       int nilai  = 0;
       
       double d = this.getNumber(rupiah);
       nilai = (int) d;
       
       return nilai;
   }

    public String getText(double harga) {

        return kursIndonesia.format(harga);
    }

}
