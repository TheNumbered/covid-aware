<?php
$username = "s2433205";
$password = "s2433205";
$database = "d2433205";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

$category = $_GET['category'];

$output=array();

if ($result = mysqli_query($link, "SELECT users.full_name, requests.request_id, requests.quantity
FROM users
LEFT JOIN requests ON users.user_id = requests.user_id
WHERE requests.category_id = '$category';
")) {
	while ($row=$result->fetch_assoc()){
		$output[]=$row;
	}
}
mysqli_close($link);
echo json_encode($output);
?>
