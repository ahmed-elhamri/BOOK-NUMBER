<?php
include_once '../dao/IDAO.php';
include_once '../connection/Connection.php';
include_once '../classes/Contact.php';

class ContactService implements IDAO {

    public function create($contact) {
        $conn = Connection::getConnection();

        $phone = $contact->getPhone();
        $name = $contact->getName();

        $sql = "INSERT INTO contacts (name, phone) VALUES (:name, :phone)";
        $stmt = $conn->prepare($sql);

        $stmt->bindParam(':name', $name);
        $stmt->bindParam(':phone', $phone);

        return $stmt->execute();
    }



    public function findByNumber($phone) {
        $conn = Connection::getConnection();
        $query = "SELECT * FROM contacts WHERE phone LIKE :phone";
        $req = $conn->prepare($query);
        $likePhone = "%$phone%"; // recherche partielle
        $req->bindParam(':phone', $likePhone);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC); // fetchAll pour plusieurs résultats
    }

    public function findAll() {
        $conn = Connection::getConnection();
        $query = "select * from contacts";
        $req = $conn->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }
}
?>
