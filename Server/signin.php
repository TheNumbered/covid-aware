<?php
$host = '127.0.0.1';
$db_name = 'd2580661';
$db_user = 's2580661';
$db_password = 's2580661';
$db_password = 's2580661';

$conn = mysqli_connect($host, $db_user, $db_password, $db_name);

if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}


$username = $_REQUEST['username'];
$password = $_REQUEST['password'];
$email = $_REQUEST['email'];


if(!empty($username)){
	if (mysqli_num_rows(mysqli_query($conn, "SELECT username FROM user WHERE username='$username'")) == 0) {
        
		if (strlen($username) >= 3 && strlen($username) <= 32) {

		        if (preg_match('/[a-zA-Z0-9_]+/', $username)) {

		                if (strlen($password) >= 6 && strlen($password) <= 60) {
		                        if (filter_var($email, FILTER_VALIDATE_EMAIL)) {

		                                if (mysqli_num_rows(mysqli_query($conn, "SELECT email FROM user WHERE email='$email'")) == 0) {

							$passwordHash = password_hash($password, PASSWORD_BCRYPT);
							$query = "INSERT INTO user (username, email, password) VALUES ('$username', '$email', '$passwordHash')";
							mysqli_query($conn, $query);
							
							//mysqli_query($conn, "SELECT id FROM user WHERE username ='$username'")['id'];

		                                        echo '{"status": "400", "words":"Logged In"}';
		                                        
		                                } else {
		                                        echo '{"status": "401", "words":"Email Already In Use"}';
		                                }
		                        }else {
		                                echo '{"status": "401", "words":"Invalid Email"}';
		                        }
		                } else {
		                        echo '{"status": "401", "words":"Your Password Must Have More Than 6 Characters"}';
		                }

		        }else {
		                echo '{"status": "401", "words":"Invalid Username"}';
		        }
		}else{
        	    echo'{"status": "401", "words":"Username too short"}';
	        }
	}else {
		echo '{"status": "401", "words":"User Already Exists"}';
	}

}else{
        echo '{"status": "401", "words":"Invalid Username"}';
}

?>
