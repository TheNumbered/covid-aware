<?php

error_reporting(E_ALL);
ini_set('display_errors', '1');

$username = "s2580661";
$password = "s2580661";
$database = "d2580661";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}


$userid = $_REQUEST['userid'];
$newpass= $_GET['newpass'];

if (strlen($newpass) >= 6 && strlen($newpass) <= 60) {
	$passwordHash = password_hash($newpass, PASSWORD_BCRYPT);
	$query = "UPDATE user SET password='$passwordHash' WHERE id='$userid'";
	mysqli_query($link, $query);
	echo('Change Password Successful');
}else{
	echo 'Password Too Short';
}


mysqli_close($link);
?>
