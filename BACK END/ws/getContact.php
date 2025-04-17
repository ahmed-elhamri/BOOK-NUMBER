<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../service/ContactService.php';

    $phone = $_POST['phone'];
    $contactDAO = new ContactService();
    $contacts = $contactDAO->findByNumber($phone);

    header('Content-Type: application/json');
    echo json_encode($contacts); // même si vide, retourne un tableau []
}

?>
