/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
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
            System.out.println("Error while parsing Rupiah Generator");
        }

        return nilai;
    }

    public int getIntNumber(String rupiah) {
        int nilai = 0;

        double d = this.getNumber(rupiah);
        nilai = (int) d;

        return nilai;
    }

    public String getText(double harga) {
    // we remove the ,00 decimal behind
        return kursIndonesia.format(harga).replace(",00", "");
    }

}
