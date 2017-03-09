<?php
	
	require_once 'include/Config.php';
	// json response array
	$response = array("error" => FALSE);
	
    //open connection to mysql db
    $connection = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE) or die("Error " . mysqli_error($connection));
	
	if (isset($_POST['category'])) {
		// receiving the post params
		$category = $_POST['category'];
		
		//fetch table rows from mysql db
		$sql = "select * from items where category='".$category."'";
		$result = mysqli_query($connection, $sql) or die("Error in Selecting " . mysqli_error($connection));

		//create an array
		$emparray = array();
		while($row =mysqli_fetch_assoc($result)){
			$emparray[] = $row;
		}
		echo json_encode($emparray);

		//close the db connection
		mysqli_close($connection);
	} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters category is missing";
    echo json_encode($response);
}	
?>
