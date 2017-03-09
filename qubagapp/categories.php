<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
	
	require_once 'include/Config.php';
    //open connection to mysql db
    $connection = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE) or die("Error " . mysqli_error($connection));

    //fetch table rows from mysql db
    $sql = "select * from categories";
    $result = mysqli_query($connection, $sql) or die("Error in Selecting " . mysqli_error($connection));

    //create an array
    $emparray = array();
    while($row =mysqli_fetch_assoc($result)){
        $emparray[] = $row;
    }
    echo json_encode($emparray);

    //close the db connection
    mysqli_close($connection);
	
?>
