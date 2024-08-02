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


$query = "SELECT check_in_date, loc_name  FROM check_in JOIN location ON check_in.location_id = location.location_id WHERE check_in.user_id= '$userid';";
$result = mysqli_query($link, $query);

if ($result) {
    if (mysqli_num_rows($result) > 0) {
        $locations = array();
        
        while ($row = mysqli_fetch_assoc($result)) {
            $locations[] = $row;
        }
        $jsonArray = json_encode($locations);
        echo '{"state":"400", "array":'.$jsonArray.'}';
    } else {
        echo '{"state" : "404"}';
    }
} else {
    echo '{"state" : "404"}';
}



?>
