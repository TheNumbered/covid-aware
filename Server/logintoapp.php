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

if(mysqli_num_rows(mysqli_query($conn, "SELECT * FROM user WHERE username = '$username'"))>0){
	$inDB = mysqli_fetch_assoc(mysqli_query($conn, "SELECT password FROM user WHERE username ='$username'"))['password'];
	if(password_verify($password, $inDB)){
		
		$user_info = array();
		$result = mysqli_query($conn, "SELECT * FROM user WHERE username = '$username'");
		while ($row = mysqli_fetch_assoc($result)) {
		$user_info['username'] = $row['username'];
		$user_info['email'] = $row['email'];
		$user_info['id'] = $row['id'];
		$user_info['state'] = "400";
		
		$userid = $row['id'];
		$cstrong = true;
		$token = bin2hex(openssl_random_pseudo_bytes(64, $cstrong));
		$hashedToken = hash('sha256', $token);
		$query = "INSERT INTO login_tokens (token, user_id) VALUES ('$hashedToken', '$userid')";
		mysqli_query($conn, $query);

		$user_info['token'] = $hashedToken;
	    }

	    echo json_encode($user_info);
	}else{
	
		echo '{"state":"402"}'; //USER EXITS JUST INCORRECT PASSWORD
	}
        
}else{
        echo '{"state":"401"} '; //user does not exist

}

mysqli_close($conn);
?>
