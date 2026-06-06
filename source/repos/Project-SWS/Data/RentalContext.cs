using Microsoft.EntityFrameworkCore;
using Project_SWS.Models;

namespace Project_SWS.Data
{
    public class RentalContext : DbContext
    {
        public DbSet<Car> Cars { get; set; }

        // --- NOWE TABELE ---
        public DbSet<Client> Clients { get; set; }
        public DbSet<Reservation> Reservations { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlite("Data Source=RentalDatabase_v2.db");
        }
    }
}