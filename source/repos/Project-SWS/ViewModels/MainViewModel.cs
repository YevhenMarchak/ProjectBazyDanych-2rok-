using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Windows.Input;
using Project_SWS.Models;
using Project_SWS.Commands;
using Project_SWS.Data;
using Microsoft.EntityFrameworkCore; // Ważne dla relacji w bazie!

namespace Project_SWS.ViewModels
{
    public class MainViewModel : ViewModelBase
    {
        // ==========================================
        //               NAWIGACJA
        // ==========================================
        private object _currentView;
        public object CurrentView
        {
            get { return _currentView; }
            set { _currentView = value; OnPropertyChanged(); }
        }
        public ICommand NavigateCarsCommand { get; set; }
        public ICommand NavigateClientsCommand { get; set; }
        public ICommand NavigateReservationsCommand { get; set; } // Nowe

        // ==========================================
        //               LISTY DANYCH
        // ==========================================
        public ObservableCollection<Car> Cars { get; set; }
        public ObservableCollection<Client> Clients { get; set; }
        public ObservableCollection<Reservation> Reservations { get; set; } // Nowe

        // ==========================================
        //         WŁAŚCIWOŚCI SAMOCHODÓW
        // ==========================================
        private Car _selectedCar;
        public Car SelectedCar { get { return _selectedCar; } set { _selectedCar = value; OnPropertyChanged(); } }
        public string NewCarBrand { get; set; } = string.Empty;
        public string NewCarModel { get; set; } = string.Empty;
        public int NewCarYear { get; set; } = 2024;
        public decimal NewCarPrice { get; set; }

        public ICommand AddCarCommand { get; set; }
        public ICommand DeleteCarCommand { get; set; }

        // ==========================================
        //          WŁAŚCIWOŚCI KLIENTÓW
        // ==========================================
        private Client _selectedClient;
        public Client SelectedClient { get { return _selectedClient; } set { _selectedClient = value; OnPropertyChanged(); } }
        public string NewClientFirstName { get; set; } = string.Empty;
        public string NewClientLastName { get; set; } = string.Empty;
        public string NewClientPhone { get; set; } = string.Empty;
        public string NewClientEmail { get; set; } = string.Empty;

        public ICommand AddClientCommand { get; set; }
        public ICommand DeleteClientCommand { get; set; }

        // ==========================================
        //          WŁAŚCIWOŚCI REZERWACJI
        // ==========================================
        private Reservation _selectedReservation;
        public Reservation SelectedReservation { get { return _selectedReservation; } set { _selectedReservation = value; OnPropertyChanged(); } }

        // Pola formularza rezerwacji
        private Client _newResClient;
        public Client NewResClient { get { return _newResClient; } set { _newResClient = value; OnPropertyChanged(); } }

        private Car _newResCar;
        public Car NewResCar { get { return _newResCar; } set { _newResCar = value; OnPropertyChanged(); } }

        private DateTime _newResStart = DateTime.Now;
        public DateTime NewResStart { get { return _newResStart; } set { _newResStart = value; OnPropertyChanged(); } }

        private DateTime _newResEnd = DateTime.Now.AddDays(1);
        public DateTime NewResEnd { get { return _newResEnd; } set { _newResEnd = value; OnPropertyChanged(); } }

        public ICommand AddReservationCommand { get; set; }
        public ICommand DeleteReservationCommand { get; set; }

        // ==========================================
        //               KONSTRUKTOR
        // ==========================================
        public MainViewModel()
        {
            // Inicjalizacja komend nawigacji
            NavigateCarsCommand = new RelayCommand(o => CurrentView = new Views.CarsView());
            NavigateClientsCommand = new RelayCommand(o => CurrentView = new Views.ClientsView());
            NavigateReservationsCommand = new RelayCommand(o => CurrentView = new Views.ReservationsView());

            CurrentView = new Views.CarsView(); // Ekran startowy

            // Ładowanie danych z bazy
            using (var db = new RentalContext())
            {
                db.Database.EnsureCreated();
                Cars = new ObservableCollection<Car>(db.Cars.ToList());
                Clients = new ObservableCollection<Client>(db.Clients.ToList());

                // POBIERANIE REZERWACJI (Include ładuje powiązane obiekty z innych tabel!)
                var resList = db.Reservations.Include(r => r.Car).Include(r => r.Client).ToList();
                Reservations = new ObservableCollection<Reservation>(resList);
            }

            // Inicjalizacja logiki przycisków
            AddCarCommand = new RelayCommand(AddCar);
            DeleteCarCommand = new RelayCommand(DeleteCar);
            AddClientCommand = new RelayCommand(AddClient);
            DeleteClientCommand = new RelayCommand(DeleteClient);
            AddReservationCommand = new RelayCommand(AddReservation);
            DeleteReservationCommand = new RelayCommand(DeleteReservation);
        }

        // ==========================================
        //               LOGIKA BIZNESOWA
        // ==========================================
        private void AddCar(object obj)
        {
            var newCar = new Car { Brand = NewCarBrand, Model = NewCarModel, Year = NewCarYear, PricePerDay = NewCarPrice, IsAvailable = true };
            using (var db = new RentalContext()) { db.Cars.Add(newCar); db.SaveChanges(); Cars.Add(newCar); }
        }

        private void DeleteCar(object obj)
        {
            if (SelectedCar != null) { using (var db = new RentalContext()) { db.Cars.Remove(SelectedCar); db.SaveChanges(); } Cars.Remove(SelectedCar); }
        }

        private void AddClient(object obj)
        {
            var newClient = new Client { FirstName = NewClientFirstName, LastName = NewClientLastName, PhoneNumber = NewClientPhone, Email = NewClientEmail };
            using (var db = new RentalContext()) { db.Clients.Add(newClient); db.SaveChanges(); Clients.Add(newClient); }
        }

        private void DeleteClient(object obj)
        {
            if (SelectedClient != null) { using (var db = new RentalContext()) { db.Clients.Remove(SelectedClient); db.SaveChanges(); } Clients.Remove(SelectedClient); }
        }

        private void AddReservation(object obj)
        {
            if (NewResClient != null && NewResCar != null)
            {
                // 1. Tworzymy rezerwację, przekazując TYLKO numery ID dla bazy danych
                var res = new Reservation
                {
                    ClientId = NewResClient.Id,
                    CarId = NewResCar.Id,
                    StartDate = NewResStart,
                    EndDate = NewResEnd
                };

                // 2. Zapisujemy w bazie (teraz EF Core wie, że dodaje tylko rezerwację)
                using (var db = new RentalContext())
                {
                    db.Reservations.Add(res);
                    db.SaveChanges();
                }

                // 3. Dopiero po zapisie dodajemy obiekty dla naszego interfejsu (żeby tabela wyświetlała nazwy)
                res.Client = NewResClient;
                res.Car = NewResCar;
                Reservations.Add(res);
            }
        }

        private void DeleteReservation(object obj)
        {
            if (SelectedReservation != null) { using (var db = new RentalContext()) { db.Reservations.Remove(SelectedReservation); db.SaveChanges(); } Reservations.Remove(SelectedReservation); }
        }
    }
}