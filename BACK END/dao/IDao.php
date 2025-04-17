<?php
interface IDAO {
    public function create($contact);
    public function findByNumber($phone);
    public function findAll();
}
?>
