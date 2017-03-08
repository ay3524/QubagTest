<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['title']) && isset($_POST['price']) && isset($_POST['size']) && isset($_POST['orderTime']) && isset($_POST['email'])) {

    // receiving the post params
    $title = $_POST['title'];
	$price = $_POST['price'];
	$size = $_POST['size'];
	$orderTime = $_POST['orderTime'];
	$email = $_POST['email'];

        // create a new order
        $order = $db->storeOrder($title, $price, $size, $orderTime, $email);
        if ($order) {
            // order stored successfully
            $response["error"] = FALSE;
            echo json_encode($response);
        } else {
            // order failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "Unknown error occurred in storing order!";
            echo json_encode($response);
        }
    }
?>

