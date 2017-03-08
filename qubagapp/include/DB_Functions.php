<?php

class DB_Functions {

    private $conn;

    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }

    // destructor
    function __destruct() {
        
    }

    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password, $phone, $address) {
        $uuid = uniqid('', true); //Assigning a unique id to every user
        $verified = '0';
        $stmt = $this->conn->prepare("INSERT INTO user_info(unique_id, name, email, password, phone, address, verified) VALUES (?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("sssssss", $uuid, $name, $email, $password, $phone, $address, $verified);
        $result = $stmt->execute();
        $stmt->close();

        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM user_info WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        } else {
            return false;
        }
    }
	
	public function storeOrder($title, $price, $size,$orderTime ,$email){
		$stmt = $this->conn->prepare("INSERT INTO orders(title, price, size, orderTime, email) VALUES (?, ?, ?, ?, ?)");
		$stmt->bind_param("sssss", $title, $price, $size, $orderTime,$email);
        $result = $stmt->execute();
        $stmt->close();
		
		// check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM user_info WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        } else {
            return false;
        }
	}

    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {

        $stmt = $this->conn->prepare("SELECT * FROM user_info WHERE email = ?");

        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password
            $user_password = $user['password'];
            // check for password equality
            if ($password == $user_password) {
                // user authentication details are correct
                return $user;
            }
        } else {
            return NULL;
        }
    }
	
	/**
     * Get verification status by email
     */
	public function getVerifyStatusByEmail($email){
		$stmt = $this->conn->prepare("SELECT * FROM user_info WHERE email = ?");

        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        } else {
            return NULL;
        }
	}
	
	/**
     * Get category list
     */
	public function getCategoryList(){
		$stmt = $this->conn->prepare("SELECT * FROM categories");
		$emparray = array();
		if ($stmt->execute()) {
			while($row =$stmt->get_result()->fetch_assoc()){
				$emparray[] = $row;
			}
				$stmt->close();

				return $emparray;
        } else {
				return NULL;
        }
	}

    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email from user_info WHERE email = ?");

        $stmt->bind_param("s", $email);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }


}

?>
