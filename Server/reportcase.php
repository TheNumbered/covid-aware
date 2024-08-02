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

$date = $_GET['date'];
$id = $_GET['id'];

// Checkreported within the last 15 days
$checkDuplicateQuery = "SELECT * FROM report WHERE user_id = '$id' AND diagnosis_date >= DATE_SUB('$date', INTERVAL 15 DAY)";
$checkDuplicateResult = mysqli_query($link, $checkDuplicateQuery);

if (mysqli_num_rows($checkDuplicateResult) > 0) {
    $response = "Error: User has already reported within the last 15 days.";
} else {
    if (isset($_GET['longitude'], $_GET['latitude'])) {
        $longitude = $_GET['longitude'];
        $latitude = $_GET['latitude'];
        $sql = "INSERT INTO report (user_id, diagnosis_date, longitude, latitude) VALUES ('$id', '$date', '$longitude', '$latitude')";
    } else {
        $sql = "INSERT INTO report (user_id, diagnosis_date) VALUES ('$id', '$date')";
    }
    if (mysqli_query($link, $sql)) {
        $response = "Successfully reported case";
    } else {
        $response = "Error: " . $sql . "<br>" . mysqli_error($link);
    }
}

mysqli_close($link);
echo $response;
?>
