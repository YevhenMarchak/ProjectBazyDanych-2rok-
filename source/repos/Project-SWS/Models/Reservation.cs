using System;

namespace Project_SWS.Models
{
    public class Reservation
    {
        public int Id { get; set; }
        public DateTime StartDate { get; set; }
        public DateTime EndDate { get; set; }
        public decimal TotalPrice { get; set; }

        // --- RELACJE (Klucze obce) ---
        // 1. Który samochód jest wypożyczony?
        public int CarId { get; set; }
        public Car Car { get; set; }

        // 2. Kto go wypożycza?
        public int ClientId { get; set; }
        public Client Client { get; set; }
    }
}