<?php
class DbOperations {
  private $con;

  function __construct()
  {


        require_once dirname(__FILE__) . '/DbConnect.php';

        $db = new DbConnect();

        $this->con = $db->connect();
  }

  //Creates a user
  public function createUser($username, $password, $email)
  {
        //Checks if user exists
        if ($this->doesUserExistByUsernameEmail($username, $email)) {
            return USER_EXISTS;
        } else {
            //Creates a SQL statement
            $stmt = $this->con->prepare("INSERT INTO user (username, password, email) VALUES (?, ?, ?)");
            //Binds parameters
            $stmt->bind_param("sss", $username, $password, $email);
            //Executes the query. If successful, returns a constant USER_CREATED, else USER_FAILURE
            if ($stmt->execute()) {
                return USER_CREATED;
            } else {
                return USER_FAILURE;
            }
        }
  }

  //Creates a chat
  public function createChat($UserID, $TherapistID)
  {
 
        if ($this->doesChatExist($UserID, $TherapistID) == false) {
            $stmt = $this->con->prepare("INSERT INTO chat (UserID, TherapistID) VALUES (?, ?)");
            $stmt->bind_param("ii", $UserID, $TherapistID);

            if ($stmt->execute()) {
                return true;
            } else {
                return false;
            }
        }
  }

  //Checks if chat exists
  public function doesChatExist($UserID, $TherapistID)
  {

        $stmt = $this->con->prepare("SELECT * FROM chat WHERE UserID = ? AND TherapistID = ?");
        $stmt->bind_param("ii", $UserID, $TherapistID);
        $stmt->execute();
        $stmt->store_result();
        return $stmt->num_rows > 0;
  }

  //Gets all chats associated with that ID
  public function getChats($ID)
  {

        $stmt = $this->con->prepare("SELECT Chat_ID, UserID, TherapistID FROM chat WHERE UserID = ? OR TherapistID = ?");
        $stmt->bind_param("ii", $ID, $ID);
        $stmt->execute();
        $stmt->bind_result($Chat_ID, $UserID, $TherapistID);
        $chats = array();
        while ($stmt->fetch()) {
            $chat = array();
            $chat['Chat_ID'] = $Chat_ID;
            $chat['UserID'] = $UserID;
            $chat['TherapistID'] = $TherapistID;
            array_push($chats, $chat);
        }
        return $chats;
  }

  //Gets the chat
  public function getChat($ID)
  {

        $stmt = $this->con->prepare("SELECT Chat_ID, UserID, TherapistID FROM chat WHERE UserID");
        $stmt->bind_param("i", $ID);
        $stmt->bind_result($Chat_ID, $UserID, $TherapistID);
        $chats = array();
        while ($stmt->fetch()) {
            $chat = array();
            $chat['Chat_ID'] = $Chat_ID;
            $chat['UserID'] = $UserID;
            $chat['TherapistID'] = $TherapistID;
            array_push($chats, $chat);
        }
        return $chats;
  }

  //Gets a full entry
  public function getFullEntry($ID, $title)
  {

        $stmt = $this->con->prepare("SELECT text FROM entry WHERE ID = ? AND title = ?");
        $stmt->bind_param("is", $ID, $title);
        $stmt->execute();
        $stmt->bind_result($text);
        $stmt->fetch();
        return $text;
  }

  //Checks if the photo exists
  public function PhotoExist($ID)
  {

        $stmt = $this->con->prepare("SELECT * FROM imageinfo WHERE ID = ?");
        $stmt->bind_param("i", $ID);
        $stmt->execute();
        $stmt->store_result();
        return $stmt->num_rows > 0;
  }

  //Updates the profile
  public function profileUpdate($ID, $path)
  {

        $stmt = $this->con->prepare("INSERT INTO imageinfo (ID, path) VALUES (?, ?)");
        $stmt->bind_param("is", $ID, $path);
        if ($stmt->execute()) {
            return true;
        } else false;
  }

  //Updates Image ID
  public function updateImageID($Image_ID, $ID)
  {

        $stmt = $this->con->prepare("UPDATE profile SET Image_ID = ? WHERE ID = ?");
        $stmt->bind_param("ii", $Image_ID, $ID);
        if ($stmt->execute()) {
            return true;
        } else return false;
  }

  //Updates Profile About
  public function updateProfileAbout($ID, $about)
  {

        $stmt = $this->con->prepare("UPDATE profile SET about = ? WHERE ID = ?");
        $stmt->bind_param("si", $about, $ID);
        if ($stmt->execute()) {
            return true;
        } else return false;
  }

  //Gets user image by ID
  public function getUserImageByID($ID)
  {

        $stmt = $this->con->prepare("SELECT Image_ID FROM imageinfo WHERE ID = ?");
        $stmt->bind_param("i", $ID);
        $stmt->execute();
        $stmt->bind_result($Image_ID);
        $stmt->fetch();
        return $Image_ID;
  }

  //Creates a profile
  public function createProfile($ID, $Image_ID, $about)
  {

        if (!$this->doesUserProfileExistByID($ID)) {
            $stmt = $this->con->prepare("INSERT INTO profile (ID, Image_ID, about) VALUES (?, ?, ?)");
            $stmt->bind_param("iis", $ID, $Image_ID, $about);
            if ($stmt->execute()) {
                return true;
            } else {
                return false;
            }
        }
  }

  //Creates a diary entry
  public function createEntry($ID, $title, $text)
  {

        $stmt = $this->con->prepare("INSERT INTO entry (ID, title, text) VALUES (?, ?, ?)");
        $stmt->bind_param("iss", $ID, $title, $text);
        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
  }

  //Adds user as a therapist
  public function addUserAsATherapist($ID, $name, $surname, $DoB, $phoneNumber, $discipline, $valid)
  {

        if ($this->doesTherapistExistByID($ID)) {
            return USER_EXISTS;
        } else {

            $stmt = $this->con->prepare("INSERT INTO therapist (ID, name, surname, DoB, phoneNumber, discipline, valid) VALUES (?, ?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("isssssi", $ID, $name, $surname, $DoB, $phoneNumber, $discipline, $valid);
            if ($stmt->execute()) {
                return USER_CREATED;
            } else {
                return USER_FAILURE;
            }
        }
  }

  //Logs the user in
  public function userLogin($username, $pass)
  {

        if ($this->doesUserExistByUsername($username)) {

            $hashed_password = $this->getUserPasswordByUsername($username);

            if (password_verify($pass, $hashed_password)) {
                return USER_AUTHENTICATED;
            } else {
                return PASSWORD_DO_NOT_MATCH;
            }
        } else {
            return USER_NOT_FOUND;
        }
  }

  //Gets the user id by username or email
  public function getUserIDByUsernameAndEmail($username, $email)
  {

        $stmt = $this->con->prepare("SELECT ID FROM user WHERE username = ? AND email = ?");
        $stmt->bind_param("ss", $username, $email);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
  }

  //Deletes the user
  public function deleteUser($ID)
  {

        $stmt = $this->con->prepare("DELETE FROM user WHERE ID = ?");
        $stmt->bind_param("i", $ID);
        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
  }

  //Gets all therapists
  public function getAllTherapists()
  {

        $stmt = $this->con->prepare("SELECT Therapist_ID, ID, name, surname, DoB, phoneNumber, discipline, valid FROM therapist WHERE valid = 1");
        $stmt->execute();
        $stmt->bind_result($Therapist_ID, $ID, $name, $surname, $DoB, $phoneNumber, $discipline, $valid);
        $therapists = array();
        while ($stmt->fetch()) {
            $therapist = array();
            $therapist['Therapist_ID'] = $Therapist_ID;
            $therapist['ID'] = $ID;
            $therapist['name'] = $name;
            $therapist['surname'] = $surname;
            $therapist['DoB'] = $DoB;
            $therapist['phoneNumber'] = $phoneNumber;
            $therapist['discipline'] = $discipline;
            $therapist['valid'] = $valid;
            array_push($therapists, $therapist);
        }
        return $therapists;
  }

  //Gets entries by ID
  public function getEntriesByID($ID)
  {

        $stmt = $this->con->prepare("SELECT title, text FROM entry WHERE ID = ?");
        $stmt->bind_param("i", $ID);
        $stmt->execute();
        $stmt->bind_result($title, $text);
        $entries = array();
        while ($stmt->fetch()) {
            $entry = array();
            $entry['title'] = $title;
            $entry['text'] = $text;
            array_push($entries, $entry);
        }
        return $entries;
  }

  //Updates user email
  public function updateUserEmail($email, $ID)
  {

        $stmt = $this->con->prepare("UPDATE user SET email = ? WHERE ID = ?");
        $stmt->bind_param("si", $email, $ID);
        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
  }

  //Gets the profile associated with the ID
  public function getProfile($ID)
  {

        $stmt = $this->con->prepare("SELECT * FROM profile WHERE ID = ?");
        $stmt->bind_param("i", $ID);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
  }

  //Gets user by username
  public function getUserByUsername($username)
  {

        $stmt = $this->con->prepare("SELECT * FROM user WHERE username = ?");
        $stmt->bind_param("s", $username);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
  }

  //Gets user username associated with the ID
  public function getUserUsernameByID($ID)
  {

        $stmt = $this->con->prepare("SELECT Username FROM user WHERE ID = ?");
        $stmt->bind_param("i", $ID);
        $stmt->execute();
        $stmt->bind_result($username);
        $stmt->fetch();
        return $username;
  }

  //Gets messages with that chat ID
  public function getMessagesByChatID($Chat_ID)
  {

        $stmt = $this->con->prepare("SELECT Message_ID, ID, message FROM message WHERE Chat_ID = ?");
        $stmt->bind_param("i", $Chat_ID);
        $stmt->execute();
        $stmt->bind_result($Message_ID, $ID, $textMessage);
        $messages = array();
        while ($stmt->fetch()) {
            $message = array();
            $message['Message_ID'] = $Message_ID;
            $message['ID'] = $ID;
            $message['message'] = $textMessage;
            array_push($messages, $message);
        }
        return $messages;
  }

  //Sends a message
  public function sendMessage($ID, $Chat_ID, $message)
  {

        $stmt = $this->con->prepare("INSERT INTO message (ID, Chat_ID, message) VALUES (?, ?, ?)");
        $stmt->bind_param("iis", $ID, $Chat_ID, $message);

        if ($stmt->execute()) {
            return true;
        } else {
            return false;
        }
  }

  //Gets chat ID
  public function getChatID($UserID, $TherapistID)
  {

        $stmt = $this->con->prepare("SELECT Chat_ID FROM chat WHERE UserID = ? OR TherapistID = ?");
        $stmt->bind_param("ii", $UserID, $TherapistID);
        $stmt->execute();
        $stmt->bind_result($Chat_ID);
        $stmt->fetch();
        return $Chat_ID;
  }

  //Gets therapist ID
  public function getTherapistByID($ID)
  {

        $stmt = $this->con->prepare("SELECT * FROM therapist WHERE ID = ?");
        $stmt->bind_param("i", $ID);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
  }

  //Updates the password
  public function updatePassword($currentPassword, $newPassword, $username)
  {

        $hashed_password = $this->getUserPasswordByUsername($username);

        if (password_verify($currentPassword, $hashed_password)) {

            $hash_password = password_hash($newPassword, PASSWORD_DEFAULT);
            $stmt = $this->con->prepare("UPDATE user SET password = ? WHERE username = ?");
            $stmt->bind_param("ss", $hash_password, $username);

            if ($stmt->execute()) {
                return PASSWORD_CHANGED;
            } else {
                return PASSWORD_NOT_CHANGED;
            }
        } else {
            return PASSWORD_DO_NOT_MATCH;
        }
  }

  //Gets username by ID
  public function getUsernameByID($ID)
  {

        $stmt = $this->con->prepare("SELECT username FROM user WHERE ID = ?");
        $stmt->bind_param("i", $ID);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
  }

  //Gets ID by username
  public function getIDByUsername($username)
  {

        $stmt = $this->con->prepare("SELECT ID FROM user WHERE username = ?");
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $stmt->bind_result($ID);
        $stmt->fetch();
        return $ID;
  }

  //Gets user password by username (the password will be hashed)
  private function getUserPasswordByUsername($username)
  {

        $stmt = $this->con->prepare("SELECT password FROM user WHERE username = ?");
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $stmt->bind_result($password);
        $stmt->fetch();
        return $password;
  }

  //Checks if user exists with that username or email
  private function doesUserExistByUsernameEmail($username, $email)
  {

        $stmt = $this->con->prepare("SELECT * FROM user WHERE username = ? OR email = ?");
        $stmt->bind_param("ss", $username, $email);
        $stmt->execute();
        $stmt->store_result();
        return $stmt->num_rows > 0;
  }

  //Checks if therapist exists with that ID
  private function doesTherapistExistByID($ID)
  {

        $stmt = $this->con->prepare("SELECT * FROM therapist WHERE ID = ?");
        $stmt->bind_param("i", $ID);
        $stmt->execute();
        $stmt->store_result();
        return $stmt->num_rows > 0;
  }

  //Checks if user profile exists with that ID
  private function doesUserProfileExistByID($ID)
  {

        $stmt = $this->con->prepare("SELECT * FROM profile WHERE ID = ?");
        $stmt->bind_param("i", $ID);
        $stmt->execute();
        $stmt->store_result();
        return $stmt->num_rows > 0;
  }

  //Checks if user exists with that username
  private function doesUserExistByUsername($username)
  {

        $stmt = $this->con->prepare("SELECT ID FROM user WHERE username = ?");
        $stmt->bind_param("s", $username); //Login with both username or email
        $stmt->execute();
        $stmt->store_result();
        return $stmt->num_rows > 0;
  }

}

