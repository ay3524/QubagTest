<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['email'])) {
	// receiving the post params
    $email = $_POST['email'];

    // get the user by email and password
    $user = $db->getVerifyStatusByEmail($email);
	
	if ($user != false) {
        // user is found
        $response["error"] = FALSE;
        $response["uid"] = $user["unique_id"];
        $response["user_info"]["name"] = $user["name"];
        $response["user_info"]["email"] = $user["email"];
		$response["user_info"]["verified"] = $user["verified"];
        echo json_encode($response);
    } else {
        // user is not found with the credentials
        $response["error"] = TRUE;
        $response["error_msg"] = "Verified credentials are wrong. Please try again!";
        echo json_encode($response);
    }
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters verified parameters is missing!";
    echo json_encode($response);
}

?>