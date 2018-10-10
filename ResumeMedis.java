/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resumemedis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author dody
 */
public class ResumeMedis {

    /**
     * @param args the command line arguments
     */
    private static final String XCONSID = "1234567"; // Kode RS dari kemenkes
    private static final String SECRETKEY = "09876"; // Password SIRS Online
    private static final String STRURL = "http://103.74.143.35/apps/sisrute/index_ci.php/services/resume/load_resume"; // URL Development

    private static String generateHmacSHA256Signature(String data, String key) {
        byte[] hmacData;

        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            hmacData = mac.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hmacData);
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(ResumeMedis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String[] args) {
        // TODO code application logic here

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String strTimeStamp = Long.toString(timestamp.getTime()).substring(0, 10);
        String generateHmacSHA256Signature = generateHmacSHA256Signature(XCONSID + "&" + strTimeStamp, SECRETKEY);

        String strNIK = "1234567890123"; // isi dengan nik pasien yang sudah ada di data resume medis
        String strTglAwal = "2018-01-01";  // tanggal awal data yang ingin diambil
        String strTglAkhir = "2018-09-14"; // tanggal akhir data yang ingin diambil

        //header for insert new
        HttpPost dataResume = new HttpPost(STRURL);
        dataResume.addHeader("Accept", "application/json");
        dataResume.addHeader("X-Cons-Id", XCONSID);
        dataResume.addHeader("X-Timestamp", strTimeStamp);
        dataResume.addHeader("X-Signature", generateHmacSHA256Signature);
        dataResume.addHeader("Content-Type", "application/json");

        try {
            String tempParams = "{\n"
                    + "    \"nik\": \"" + strNIK + "\",\n"
                    + "    \"tgl_awal_masuk\": \"" + strTglAwal + "\",\n"
                    + "    \"tgl_akhir_masuk\": \"" + strTglAkhir + "\"\n"
                    + "  }";
            StringEntity params = new StringEntity(tempParams, "UTF-8");
            dataResume.setEntity(params);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(dataResume);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            System.out.println(content);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(ResumeMedis.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataResume.releaseConnection();
        /*                     
        System.out.println("X-Cons-Id: " + XCONSID);
        System.out.println("X-Timestamp: " + strTimeStamp);
        System.out.println("X-Signature: " + generateHmacSHA256Signature);
        System.out.println("Content-Type: application/json");
         */
    }

}
