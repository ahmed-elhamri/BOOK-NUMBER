<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../connection/Connection.php';
    include_once '../service/ContactService.php';

    // Receive data from POST request
    $name = $_POST['name'];
    $phone = $_POST['phone'];
    print_r($name);

    $contact = new Contact($name, $phone);
    $contactService = new ContactService();
    if ($contactService->create($contact)) {
        echo json_encode(["message" => "Contact created successfully"]);
    } else {
        echo json_encode(["message" => "Failed to create contact"]);
    }
}
?>
