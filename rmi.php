<?php
$XCONSID = "1234567"; // Kode RS dari kemenkes
$SECRETKEY = "09876"; // Password SIRS Online
$STRURL = "http://103.74.143.35/apps/sisrute/index_ci.php/services/resume/load_resume"; // URL Development

$strNIK = "3000000000000001"; // isi dengan nik pasien yang sudah ada di data resume medis
$strTglAwal = "2018-01-01";  // tanggal awal data yang ingin diambil
$strTglAkhir = "2018-09-14"; // tanggal akhir data yang ingin diambil

$strTimeStamp = time();
$generateHmacSHA256Signature = base64_encode(hash_hmac('sha256', $XCONSID . "&" . $strTimeStamp, $SECRETKEY, true));

$process = curl_init($STRURL);
//setup request to send json via POST
$data = array(
    'nik' => $strNIK,
    'tgl_awal_masuk' => $strTglAwal,
    'tgl_akhir_masuk' => $strTglAkhir
);

//attach encoded JSON string to the POST fields
curl_setopt($process, CURLOPT_POSTFIELDS, json_encode($data));
curl_setopt($process, CURLOPT_HTTPHEADER, array("Content-Type: application/json",
    "X-Cons-Id:$XCONSID",
    "X-Timestamp: $strTimeStamp",
    "X-Signature: $generateHmacSHA256Signature"));
curl_setopt($process, CURLOPT_POST, true);
curl_setopt($process, CURLOPT_RETURNTRANSFER, TRUE);
$result = curl_exec($process);
curl_close($process);
echo $result;

/*
  echo "X-Cons-Id: " . $XCONSID;
  echo "X-Timestamp: " . $strTimeStamp;
  echo "X-Signature: " . $generateHmacSHA256Signature;
  echo "Content-Type: application/json";
 */
?>
