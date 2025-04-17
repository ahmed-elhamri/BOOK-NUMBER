<?php
class Contact {
    private $id;
    private $name;
    private $phone;

    public function __construct($name, $phone, $id = null) {
        $this->id = $id;
        $this->name = $name;
        $this->phone = $phone;
    }

    public function getId() {
        return $this->id;
    }

    public function getName() {
        return $this->name;
    }

    public function getPhone() {
        return $this->phone;
    }
}
?>
