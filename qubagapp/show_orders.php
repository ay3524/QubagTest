<?php
	
		// json response array
		$response = array("error" => FALSE);
	
		//open connection to mysql db
		$connection = mysqli_connect("localhost","root","","webappdb") or die("Error " . mysqli_error($connection));
	
	if (isset($_POST['email'])) {
		// receiving the post params
		$email = $_POST['email'];
		
		//fetch table rows from mysql db
		$sql = "select * from orders where email='".$email."'";
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