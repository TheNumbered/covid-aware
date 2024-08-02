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

$mytoken = $_GET['token'];
$theQuery = mysqli_query($conn, "SELECT * FROM login_tokens WHERE token = '$mytoken'");
if(mysqli_num_rows(mysqli_query($conn, "SELECT * FROM login_tokens WHERE token = '$mytoken'"))>0){
		
		$hisid = mysqli_fetch_assoc($theQuery)['user_id'];
		$user_info = array();
		$result = mysqli_query($conn, "SELECT * FROM user WHERE id = '$hisid'");
		while ($row = mysqli_fetch_assoc($result)) {
			$user_info['username'] = $row['username'];
			$user_info['email'] = $row['email'];
			$user_info['id'] = $row['id'];
			$user_info['state'] = "400";
	    	}
	    	echo json_encode($user_info);
        
}else{
        echo '{"state":"401","res":"WRONG TOKEN"}'; 

}
mysqli_close($conn);
?>
