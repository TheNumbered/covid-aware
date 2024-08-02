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

$query = "SELECT *, 
(SELECT COUNT(DISTINCT report.user_id) 
FROM check_in 
LEFT JOIN report ON report.user_id = check_in.user_id 
WHERE check_in.location_id = location.location_id 
    AND check_in.check_in_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 15 DAY) AND CURDATE()
    AND report.diagnosis_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 15 DAY) AND CURDATE()
) AS infected
FROM location ORDER BY infected DESC;";

$result = mysqli_query($link, $query);
if ($result) {
    if (mysqli_num_rows($result) > 0) {
        $locations = array();

        while ($row = mysqli_fetch_assoc($result)) {
            $locations[] = $row;
        }
        $jsonArray = json_encode($locations);
        echo $jsonArray;
    } else {
        echo "No locations found.";
    }
} else {
    echo "Error: " . mysqli_error($link);
}

mysqli_close($link);
?>

