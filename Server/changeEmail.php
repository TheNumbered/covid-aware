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
$newemail= $_GET['newemail'];

if (filter_var($newemail, FILTER_VALIDATE_EMAIL)) {
	$query = "UPDATE user SET email='$newemail' WHERE id='$userid'";
	mysqli_query($link, $query);
	echo('Change Email Successful');
}else{
	echo 'Invalid Email';
}


mysqli_close($link);
?>
