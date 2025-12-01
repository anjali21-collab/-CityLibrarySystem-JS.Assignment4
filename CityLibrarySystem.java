import java.io.*;
import java.util.*;

class Book implements Comparable<Book> {
    int bookId;
    String title;
    String author;
    String category;
    boolean isIssued;

    Book(int bookId, String title, String author, String category, boolean isIssued) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isIssued = isIssued;
    }

    public void displayBookDetails() {
        System.out.println("ID: " + bookId + ", Title: " + title + ", Author: " + author +
                ", Category: " + category + ", Issued: " + isIssued);
    }

    public void markAsIssued() { isIssued = true; }
    public void markAsReturned() { isIssued = false; }

    @Override
    public int compareTo(Book b) {
        return this.title.compareToIgnoreCase(b.title); // Sort by title
    }
}

class Member {
    int memberId;
    String name;
    String email;
    List<Integer> issuedBooks = new ArrayList<>();

    Member(int memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }

    public void displayMemberDetails() {
        System.out.println("Member ID: " + memberId + ", Name: " + name +
                ", Email: " + email + ", Issued Books: " + issuedBooks);
    }

    public void addIssuedBook(int bookId) { issuedBooks.add(bookId); }
    public void returnIssuedBook(int bookId) { issuedBooks.remove(Integer.valueOf(bookId)); }
}

public class CityLibrarySystem {
    Map<Integer, Book> books = new HashMap<>();
    Map<Integer, Member> members = new HashMap<>();

    Scanner sc = new Scanner(System.in);

    public CityLibrarySystem() {
        loadFromFile();
    }

    // ---- Add Book ----
    void addBook() {
        System.out.print("Enter Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author: ");
        String author = sc.nextLine();
        System.out.print("Enter Category: ");
        String category = sc.nextLine();

        int id = books.size() + 101;
        books.put(id, new Book(id, title, author, category, false));

        saveToFile();
        System.out.println("Book added with ID: " + id);
    }

    // ---- Add Member ----
    void addMember() {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        int id = members.size() + 1;
        members.put(id, new Member(id, name, email));

        saveToFile();
        System.out.println("Member added with ID: " + id);
    }

    // ---- Issue Book ----
    void issueBook() {
        System.out.print("Enter Book ID: ");
        int bookId = Integer.parseInt(sc.nextLine());

        System.out.print("Enter Member ID: ");
        int memberId = Integer.parseInt(sc.nextLine());

        if (!books.containsKey(bookId) || !members.containsKey(memberId)) {
            System.out.println("Invalid IDs.");
            return;
        }

        Book b = books.get(bookId);

        if (b.isIssued) {
            System.out.println("Book already issued.");
            return;
        }

        b.markAsIssued();
        members.get(memberId).addIssuedBook(bookId);

        saveToFile();
        System.out.println("Book issued.");
    }

    // ---- Return Book ----
    void returnBook() {
        System.out.print("Enter Book ID: ");
        int bookId = Integer.parseInt(sc.nextLine());

        if (!books.containsKey(bookId)) {
            System.out.println("Invalid Book ID.");
            return;
        }

        books.get(bookId).markAsReturned();

        for (Member m : members.values()) {
            if (m.issuedBooks.contains(bookId)) {
                m.returnIssuedBook(bookId);
            }
        }

        saveToFile();
        System.out.println("Book returned.");
    }

    // ---- Search books ----
    void searchBooks() {
        System.out.print("Search by title/author/category: ");
        String key = sc.nextLine().toLowerCase();

        for (Book b : books.values()) {
            if (b.title.toLowerCase().contains(key) ||
                b.author.toLowerCase().contains(key) ||
                b.category.toLowerCase().contains(key)) {
                b.displayBookDetails();
            }
        }
    }

    // ---- Sort books ----
    void sortBooks() {
        List<Book> list = new ArrayList<>(books.values());

        System.out.println("1. Sort by Title  2. Sort by Author");
        int choice = Integer.parseInt(sc.nextLine());

        if (choice == 1) {
            Collections.sort(list);
        } else {
            list.sort(Comparator.comparing(b -> b.author.toLowerCase()));
        }

        for (Book b : list) b.displayBookDetails();
    }

    // ---- Load data ----
    void loadFromFile() {
        try {
            File bookFile = new File("books.txt");
            File memberFile = new File("members.txt");

            if (!bookFile.exists()) bookFile.createNewFile();
            if (!memberFile.exists()) memberFile.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(bookFile));
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                int id = Integer.parseInt(p[0]);
                books.put(id, new Book(id, p[1], p[2], p[3], Boolean.parseBoolean(p[4])));
            }
            br.close();

            br = new BufferedReader(new FileReader(memberFile));
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                int id = Integer.parseInt(p[0]);
                Member m = new Member(id, p[1], p[2]);

                if (p.length > 3) {
                    String[] issued = p[3].split(" ");
                    for (String s : issued) {
                        if (!s.isEmpty()) m.issuedBooks.add(Integer.parseInt(s));
                    }
                }
                members.put(id, m);
            }
            br.close();

        } catch (Exception e) {
            System.out.println("Load error.");
        }
    }

    // ---- Save data ----
    void saveToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("books.txt"));
            for (Book b : books.values()) {
                bw.write(b.bookId + "," + b.title + "," + b.author + "," + b.category + "," + b.isIssued);
                bw.newLine();
            }
            bw.close();

            bw = new BufferedWriter(new FileWriter("members.txt"));
            for (Member m : members.values()) {
                bw.write(m.memberId + "," + m.name + "," + m.email + ",");
                for (int id : m.issuedBooks) bw.write(id + " ");
                bw.newLine();
            }
            bw.close();

        } catch (Exception e) {
            System.out.println("Save error.");
        }
    }

    // ---- Main Menu ----
    void mainMenu() {
        while (true) {
            System.out.println("\n1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. Sort Books");
            System.out.println("7. Exit");
            System.out.print("Enter Choice: ");

            int ch = Integer.parseInt(sc.nextLine());

            switch (ch) {
                case 1 -> addBook();
                case 2 -> addMember();
                case 3 -> issueBook();
                case 4 -> returnBook();
                case 5 -> searchBooks();
                case 6 -> sortBooks();
                case 7 -> { saveToFile(); System.out.println("Exit."); return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    public static void main(String[] args) {
        new CityLibrarySystem().mainMenu();
    }
}
