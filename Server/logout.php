<?php
$username = $_REQUEST['username'];
$password = $_REQUEST['password'];

error_reporting(E_ALL);
ini_set('display_errors', '1');
$host = '127.0.0.1';
$db_name = 'd2580661';
$db_user = 's2580661';
$db_password = 's2580661';
$db_password = 's2580661';

$conn = mysqli_connect($host, $db_user, $db_password, $db_name);

if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$mytoken = $_GET['token'];
$theQuery = mysqli_query($conn, "SELECT * FROM login_tokens WHERE token = '$mytoken'");

mysqli_query($conn, "DELETE FROM login_tokens WHERE token='$mytoken'");

echo('{"state":"400"}');

mysqli_close($conn);
?>
