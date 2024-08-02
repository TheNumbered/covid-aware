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

$locationID = $_GET['location_id'];
$date = $_GET['date'];
$id = $_GET['id'];

if($date > date("Y-m-d")){
	$response = "You cannot check-in tomorrow";
	
}else{
	
	$checkInQuery = "SELECT COUNT(*) AS num_check_ins FROM check_in WHERE user_id = '$id' AND check_in_date = '$date' AND location_id = '$locationID'";
	$checkInResult = mysqli_query($link, $checkInQuery);
	$checkInData = mysqli_fetch_assoc($checkInResult);
	$numCheckIns = $checkInData['num_check_ins'];

	if ($numCheckIns > 0) {
	    $response = "You have already checked in today";
	} else {
	    $sql = "INSERT INTO check_in (location_id, check_in_date, user_id) VALUES ('$locationID', '$date', '$id')";

	    if (mysqli_query($link, $sql)) {
		$response = "Check-in successful";
	    } else {
		$response = "An error occurred during check-in. Please try again later.";
	    }
	}
}

	
mysqli_close($link);
echo $response;
?>