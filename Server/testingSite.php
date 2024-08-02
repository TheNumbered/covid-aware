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

$user_Id = $_GET["user_id"];
$checkQuery = "SELECT check_in.check_in_date, location.loc_name, infected_locations.infected
FROM check_in INNER JOIN location ON check_in.location_id = location.location_id
INNER JOIN (
SELECT check_in.location_id, COUNT(DISTINCT report.user_id) AS infected
    FROM check_in INNER JOIN report ON check_in.user_id = report.user_id
    WHERE report.diagnosis_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 15 DAY) AND CURDATE()
    GROUP BY check_in.location_id
) AS infected_locations ON check_in.location_id = infected_locations.location_id
WHERE check_in.user_id = $user_Id
ORDER BY infected_locations.infected DESC;";
$checkResult = mysqli_query($link, $checkQuery);

if ($checkResult) {
    if (mysqli_num_rows($checkResult) > 0) {
        $query = "SELECT testingSite.*,location.*
        FROM testingSite
        INNER JOIN location ON testingSite.location_id = location.location_id";

        $result = mysqli_query($link, $query);
        if ($result) {
            if (mysqli_num_rows($result) > 0) {
                $testingSites = array();

                while ($row = mysqli_fetch_assoc($result)) {
                    $testingSites[] = $row;
                }
                $jsonArray = json_encode($testingSites);
                echo $jsonArray;
            } else {
                echo "No testing sites found.";
            }
        } else {
            echo "Error: " . mysqli_error($link);
        }
    } else {
        echo "User has not been to any location.";
    }
} else {
    echo "Error: " . mysqli_error($link);
}

mysqli_close($link);
?>

