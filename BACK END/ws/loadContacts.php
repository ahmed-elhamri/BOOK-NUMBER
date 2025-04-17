<?php
if ($_SERVER["REQUEST_METHOD"] == "GET") {
    include_once '../service/ContactService.php';

    $contactService = new ContactService();
    $contacts = $contactService->findAll();

    header('Content-Type: application/json');
    echo json_encode($contacts);
}
?>
