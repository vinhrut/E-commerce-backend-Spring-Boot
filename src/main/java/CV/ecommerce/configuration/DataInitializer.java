package CV.ecommerce.configuration;

import CV.ecommerce.entity.Product;
import CV.ecommerce.entity.ProductSize;
import CV.ecommerce.entity.User;
import CV.ecommerce.enums.Role;
import CV.ecommerce.repository.ProductRepository;
import CV.ecommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DataInitializer implements CommandLineRunner {

    UserRepository userRepository;
    ProductRepository productRepository;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("=== Bắt đầu kiểm tra khởi tạo dữ liệu ===");
        initAdmin();
        initUsers();
        initProducts();
        log.info("=== Hoàn tất kiểm tra khởi tạo dữ liệu ===");
    }

    // ─────────────────────────────────────────────────────────────
    // ADMIN
    // ─────────────────────────────────────────────────────────────
    private void initAdmin() {
        if (!userRepository.existsByRole(Role.ADMIN)) {
            User admin = User.builder()
                    .fullName("Quản trị viên")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("admin1234"))
                    .phone("0901234567")
                    .role(Role.ADMIN)
                    .lockUser(false)
                    .build();
            userRepository.save(admin);
            log.warn("✅ Tài khoản admin đã được tạo: admin@ecommerce.com / admin1234 — Hãy đổi mật khẩu ngay!");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // USERS
    // ─────────────────────────────────────────────────────────────
    private void initUsers() {
        if (userRepository.count() > 1) return;
        log.info("Khởi tạo người dùng mẫu...");

        List<User> users = Arrays.asList(
            User.builder().fullName("Nguyễn Văn An").email("an.nguyen@gmail.com")
                    .password(passwordEncoder.encode("password123")).phone("0912345601")
                    .role(Role.USER).lockUser(false).build(),

            User.builder().fullName("Trần Thị Bích").email("bich.tran@gmail.com")
                    .password(passwordEncoder.encode("password123")).phone("0912345602")
                    .role(Role.USER).lockUser(false).build(),

            User.builder().fullName("Lê Minh Cường").email("cuong.le@gmail.com")
                    .password(passwordEncoder.encode("password123")).phone("0912345603")
                    .role(Role.USER).lockUser(false).build(),

            User.builder().fullName("Phạm Thu Hà").email("ha.pham@gmail.com")
                    .password(passwordEncoder.encode("password123")).phone("0912345604")
                    .role(Role.USER).lockUser(false).build(),

            User.builder().fullName("Hoàng Đức Huy").email("huy.hoang@gmail.com")
                    .password(passwordEncoder.encode("password123")).phone("0912345605")
                    .role(Role.USER).lockUser(false).build(),

            User.builder().fullName("Vũ Thị Lan").email("lan.vu@gmail.com")
                    .password(passwordEncoder.encode("password123")).phone("0912345606")
                    .role(Role.USER).lockUser(false).build(),

            User.builder().fullName("Đặng Quốc Mạnh").email("manh.dang@gmail.com")
                    .password(passwordEncoder.encode("password123")).phone("0912345607")
                    .role(Role.USER).lockUser(false).build(),

            User.builder().fullName("Bùi Thị Ngọc").email("ngoc.bui@gmail.com")
                    .password(passwordEncoder.encode("password123")).phone("0912345608")
                    .role(Role.USER).lockUser(false).build(),

            User.builder().fullName("Ngô Thanh Phong").email("phong.ngo@gmail.com")
                    .password(passwordEncoder.encode("password123")).phone("0912345609")
                    .role(Role.USER).lockUser(false).build(),

            User.builder().fullName("Đinh Thị Quỳnh").email("quynh.dinh@gmail.com")
                    .password(passwordEncoder.encode("password123")).phone("0912345610")
                    .role(Role.USER).lockUser(false).build()
        );

        users.forEach(u -> {
            if (!userRepository.existsByEmail(u.getEmail())) {
                userRepository.save(u);
            }
        });
        log.info("✅ Đã khởi tạo {} người dùng mẫu.", users.size());
    }

    // ─────────────────────────────────────────────────────────────
    // PRODUCTS — 12 sản phẩm giày thực tế
    // ─────────────────────────────────────────────────────────────
    private void initProducts() {
        if (productRepository.count() > 0) return;
        log.info("Khởi tạo sản phẩm giày...");

        // ---------- 1. Nike Air Force 1 '07 ----------
        Product p1 = new Product();
        p1.setName("Nike Air Force 1 '07 Low Trắng");
        p1.setPrice(2_750_000.0);
        p1.setType("Giày thể thao");
        p1.setMaterial("Da tổng hợp cao cấp, đế Air đệm khí");
        p1.setDescription(
            "Nike Air Force 1 '07 là biểu tượng thời trang đường phố với thiết kế trắng tinh khôi. " +
            "Phần upper (mũ giày) được làm từ da tổng hợp cao cấp với độ bền vượt trội, dễ vệ sinh. " +
            "Công nghệ đệm Nike Air ở gót giúp hấp thụ lực tác động, mang lại cảm giác êm ái suốt cả ngày. " +
            "Đế ngoài cao su nguyên khối với họa tiết pivot circle tạo độ bám tốt trên nhiều bề mặt. " +
            "Phù hợp cho phong cách streetwear, thường ngày hoặc kết hợp cùng trang phục casual."
        );
        p1.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?w=600&q=80",
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80",
            "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600&q=80"
        ));
        p1.setSizes(Arrays.asList(
            ProductSize.builder().product(p1).sizeName("39").quantity(18).build(),
            ProductSize.builder().product(p1).sizeName("40").quantity(25).build(),
            ProductSize.builder().product(p1).sizeName("41").quantity(30).build(),
            ProductSize.builder().product(p1).sizeName("42").quantity(22).build(),
            ProductSize.builder().product(p1).sizeName("43").quantity(15).build(),
            ProductSize.builder().product(p1).sizeName("44").quantity(10).build()
        ));
        p1.setDeleted(false);

        // ---------- 2. Adidas Stan Smith ----------
        Product p2 = new Product();
        p2.setName("Adidas Stan Smith Xanh Lá");
        p2.setPrice(2_450_000.0);
        p2.setType("Giày lifestyle");
        p2.setMaterial("Da tự nhiên, đế OrthoLite");
        p2.setDescription(
            "Adidas Stan Smith là đôi giày tennis huyền thoại ra mắt năm 1971, nay trở thành biểu tượng thời trang toàn cầu. " +
            "Phần upper làm từ da tự nhiên mềm mại, thoáng khí với điểm nhấn 3 sọc đặc trưng bên thân. " +
            "Lót trong OrthoLite mang lại cảm giác êm chân và khả năng chống mùi hiệu quả. " +
            "Thiết kế tối giản, màu trắng kết hợp gót xanh lá cây dễ phối cùng nhiều outfit từ casual đến smart casual. " +
            "Lý tưởng cho người yêu phong cách tối giản, thanh lịch mỗi ngày."
        );
        p2.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1584735175315-9d5df23be7be?w=600&q=80",
            "https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=600&q=80",
            "https://images.unsplash.com/photo-1556048219-bb6978360b84?w=600&q=80"
        ));
        p2.setSizes(Arrays.asList(
            ProductSize.builder().product(p2).sizeName("39").quantity(12).build(),
            ProductSize.builder().product(p2).sizeName("40").quantity(20).build(),
            ProductSize.builder().product(p2).sizeName("41").quantity(28).build(),
            ProductSize.builder().product(p2).sizeName("42").quantity(20).build(),
            ProductSize.builder().product(p2).sizeName("43").quantity(14).build(),
            ProductSize.builder().product(p2).sizeName("44").quantity(8).build()
        ));
        p2.setDeleted(false);

        // ---------- 3. New Balance 574 ----------
        Product p3 = new Product();
        p3.setName("New Balance 574 Classic Xám Tro");
        p3.setPrice(2_950_000.0);
        p3.setType("Giày thể thao");
        p3.setMaterial("Vải lưới kết hợp da lộn, đế ENCAP");
        p3.setDescription(
            "New Balance 574 là mẫu giày cổ điển với hơn 30 năm lịch sử, được tái sinh trong phong cách retro hiện đại. " +
            "Upper kết hợp giữa vải mesh thoáng khí và da lộn bền chắc tạo nên vẻ ngoài phong phú về chất liệu. " +
            "Công nghệ đệm ENCAP độc quyền của New Balance kết hợp gel polyurethane và lớp EVA bên ngoài " +
            "giúp giảm chấn tốt, hỗ trợ tốt cho hoạt động cả ngày. " +
            "Màu xám tro trung tính dễ phối đồ, phù hợp cho cả nam và nữ. " +
            "Đây là lựa chọn hoàn hảo cho ai yêu thích phong cách retro sneaker Mỹ."
        );
        p3.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1539185441755-769473a23570?w=600&q=80",
            "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=600&q=80",
            "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?w=600&q=80"
        ));
        p3.setSizes(Arrays.asList(
            ProductSize.builder().product(p3).sizeName("39").quantity(10).build(),
            ProductSize.builder().product(p3).sizeName("40").quantity(18).build(),
            ProductSize.builder().product(p3).sizeName("41").quantity(25).build(),
            ProductSize.builder().product(p3).sizeName("42").quantity(25).build(),
            ProductSize.builder().product(p3).sizeName("43").quantity(17).build(),
            ProductSize.builder().product(p3).sizeName("44").quantity(9).build()
        ));
        p3.setDeleted(false);

        // ---------- 4. Nike Air Max 270 ----------
        Product p4 = new Product();
        p4.setName("Nike Air Max 270 Đen Trắng");
        p4.setPrice(3_900_000.0);
        p4.setType("Giày thể thao");
        p4.setMaterial("Vải Flyknit, đế Air Max 270 độ");
        p4.setDescription(
            "Nike Air Max 270 gây ấn tượng mạnh ngay từ cái nhìn đầu tiên với túi khí Max Air cao nhất lịch sử — 270 độ. " +
            "Upper dệt Flyknit ôm sát bàn chân như một chiếc tất thứ hai, thoáng khí và co giãn tốt. " +
            "Túi khí khổng lồ ở gót không chỉ là điểm nhấn thẩm mỹ mà còn hấp thụ lực tác động vượt trội, " +
            "mang lại sự êm ái trong suốt ngày dài đứng hay di chuyển nhiều. " +
            "Đế foam React nhẹ và đàn hồi cao, kết hợp cùng túi khí tạo nên cảm giác bước chân như trên mây. " +
            "Phù hợp cả cho gym nhẹ, đi dạo và phong cách streetwear năng động."
        );
        p4.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=600&q=80",
            "https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=600&q=80",
            "https://images.unsplash.com/photo-1598971457999-ca4ef48a9a71?w=600&q=80"
        ));
        p4.setSizes(Arrays.asList(
            ProductSize.builder().product(p4).sizeName("39").quantity(8).build(),
            ProductSize.builder().product(p4).sizeName("40").quantity(15).build(),
            ProductSize.builder().product(p4).sizeName("41").quantity(20).build(),
            ProductSize.builder().product(p4).sizeName("42").quantity(22).build(),
            ProductSize.builder().product(p4).sizeName("43").quantity(16).build(),
            ProductSize.builder().product(p4).sizeName("44").quantity(7).build()
        ));
        p4.setDeleted(false);

        // ---------- 5. Converse Chuck Taylor All Star ----------
        Product p5 = new Product();
        p5.setName("Converse Chuck Taylor All Star 70 Đen");
        p5.setPrice(1_850_000.0);
        p5.setType("Giày cao cổ");
        p5.setMaterial("Canvas bạt dày, đế cao su lưu hóa");
        p5.setDescription(
            "Converse Chuck Taylor All Star 70 là phiên bản nâng cấp của đôi giày canvas huyền thoại, " +
            "ra đời từ năm 1917 và vẫn là biểu tượng của thế hệ trẻ sáng tạo đến ngày nay. " +
            "Canvas bạt dày hơn mẫu cổ điển, cổ giày cao bọc lót êm ái bảo vệ mắt cá chân. " +
            "Đế cao su lưu hóa với họa tiết gai chạy vòng quanh tăng độ bám và tuổi thọ. " +
            "Điểm nhấn là miếng đệm gót được nâng cấp so với phiên bản gốc, " +
            "giúp người đi cảm thấy thoải mái hơn trong thời gian dài. " +
            "Màu đen tuyền cổ điển, dễ phối đồ từ quần jeans rách đến váy thun đơn giản."
        );
        p5.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1463100099107-aa0980c362e6?w=600&q=80",
            "https://images.unsplash.com/photo-1511556532299-8f662fc26c06?w=600&q=80",
            "https://images.unsplash.com/photo-1607522370275-f6d6b1a8d446?w=600&q=80"
        ));
        p5.setSizes(Arrays.asList(
            ProductSize.builder().product(p5).sizeName("36").quantity(15).build(),
            ProductSize.builder().product(p5).sizeName("37").quantity(20).build(),
            ProductSize.builder().product(p5).sizeName("38").quantity(25).build(),
            ProductSize.builder().product(p5).sizeName("39").quantity(30).build(),
            ProductSize.builder().product(p5).sizeName("40").quantity(22).build(),
            ProductSize.builder().product(p5).sizeName("41").quantity(12).build()
        ));
        p5.setDeleted(false);

        // ---------- 6. Adidas Ultraboost 22 ----------
        Product p6 = new Product();
        p6.setName("Adidas Ultraboost 22 Chạy Bộ Xanh Dương");
        p6.setPrice(4_500_000.0);
        p6.setType("Giày chạy bộ");
        p6.setMaterial("Primeknit+, đế Boost 100%");
        p6.setDescription(
            "Adidas Ultraboost 22 được thiết kế đặc biệt cho runner với công nghệ tiên tiến nhất từ Adidas. " +
            "Upper Primeknit+ dệt tích hợp ôm sát bàn chân với hệ thống đường đan được kỹ thuật số hóa " +
            "theo từng vùng chịu lực khi chạy. " +
            "Đệm Boost 100% — vật liệu đặc trưng của Adidas — trả lại tới 87% năng lượng mỗi bước chân, " +
            "giảm mệt mỏi và tăng hiệu suất chạy đường dài. " +
            "Hệ thống Torsion System ở giữa đế hỗ trợ bàn chân uốn theo địa hình tự nhiên. " +
            "Phù hợp cho tập luyện hàng ngày, chạy bộ buổi sáng và các giải chạy không chuyên."
        );
        p6.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=600&q=80",
            "https://images.unsplash.com/photo-1590736969596-701ef73cef13?w=600&q=80",
            "https://images.unsplash.com/photo-1543508282-6319a3e2621f?w=600&q=80"
        ));
        p6.setSizes(Arrays.asList(
            ProductSize.builder().product(p6).sizeName("39").quantity(12).build(),
            ProductSize.builder().product(p6).sizeName("40").quantity(18).build(),
            ProductSize.builder().product(p6).sizeName("41").quantity(22).build(),
            ProductSize.builder().product(p6).sizeName("42").quantity(20).build(),
            ProductSize.builder().product(p6).sizeName("43").quantity(14).build(),
            ProductSize.builder().product(p6).sizeName("44").quantity(6).build()
        ));
        p6.setDeleted(false);

        // ---------- 7. Vans Old Skool ----------
        Product p7 = new Product();
        p7.setName("Vans Old Skool Đen Trắng Cổ Điển");
        p7.setPrice(1_650_000.0);
        p7.setType("Giày skate");
        p7.setMaterial("Canvas và da lộn, đế cao su waffle");
        p7.setDescription(
            "Vans Old Skool ra mắt năm 1977 là đôi giày đầu tiên của Vans có sọc Jazz Stripe nổi tiếng — " +
            "nay trở thành giày skate huyền thoại được yêu thích toàn cầu. " +
            "Phần upper kết hợp canvas và da lộn tạo nên kết cấu bền bỉ, chịu mài mòn tốt trên ván trượt. " +
            "Lót trong WaffleGrip tăng cường kết nối cảm giác bàn chân với ván, " +
            "đế cao su waffle đặc trưng cho bám dính tuyệt vời. " +
            "Thiết kế cổ thấp thoải mái, dễ vận động. Màu đen trắng phong phú, " +
            "dễ phối với quần short, jogger hay outfit skate cổ điển."
        );
        p7.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1508766703222-ee8c8d69c7ee?w=600&q=80",
            "https://images.unsplash.com/photo-1514989940723-e8e51635b782?w=600&q=80",
            "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=600&q=80"
        ));
        p7.setSizes(Arrays.asList(
            ProductSize.builder().product(p7).sizeName("38").quantity(14).build(),
            ProductSize.builder().product(p7).sizeName("39").quantity(22).build(),
            ProductSize.builder().product(p7).sizeName("40").quantity(30).build(),
            ProductSize.builder().product(p7).sizeName("41").quantity(28).build(),
            ProductSize.builder().product(p7).sizeName("42").quantity(18).build(),
            ProductSize.builder().product(p7).sizeName("43").quantity(10).build()
        ));
        p7.setDeleted(false);

        // ---------- 8. Puma RS-X ----------
        Product p8 = new Product();
        p8.setName("Puma RS-X Reinvention Trắng Đỏ");
        p8.setPrice(2_350_000.0);
        p8.setType("Giày thể thao");
        p8.setMaterial("Da tổng hợp và vải mesh, đế RS (Running System)");
        p8.setDescription(
            "Puma RS-X Reinvention lấy cảm hứng từ công nghệ Running System (RS) của Puma những năm 1980 " +
            "và tái hiện với ngôn ngữ thiết kế chunky hiện đại đang làm mưa làm gió. " +
            "Đế dày chia nhiều tầng theo màu sắc tương phản tạo hiệu ứng thị giác mạnh mẽ. " +
            "Upper kết hợp lớp da tổng hợp ở các vùng cần hỗ trợ và vải mesh thoáng khí ở mu bàn chân. " +
            "Lớp đệm RS ở đế giữa phân tán lực tác động hiệu quả. " +
            "Màu trắng đỏ bắt mắt, phù hợp cho bạn muốn nổi bật giữa đám đông với phong cách retro-future."
        );
        p8.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1575537302964-96cd47c06b1b?w=600&q=80",
            "https://images.unsplash.com/photo-1622183110261-9dab03bb3e0a?w=600&q=80",
            "https://images.unsplash.com/photo-1595341888016-a392ef81b7de?w=600&q=80"
        ));
        p8.setSizes(Arrays.asList(
            ProductSize.builder().product(p8).sizeName("39").quantity(10).build(),
            ProductSize.builder().product(p8).sizeName("40").quantity(16).build(),
            ProductSize.builder().product(p8).sizeName("41").quantity(20).build(),
            ProductSize.builder().product(p8).sizeName("42").quantity(18).build(),
            ProductSize.builder().product(p8).sizeName("43").quantity(12).build(),
            ProductSize.builder().product(p8).sizeName("44").quantity(6).build()
        ));
        p8.setDeleted(false);

        // ---------- 9. Nike React Infinity Run ----------
        Product p9 = new Product();
        p9.setName("Nike React Infinity Run Flyknit 3 Cam");
        p9.setPrice(3_500_000.0);
        p9.setType("Giày chạy bộ");
        p9.setMaterial("Vải Flyknit, đế React foam, InfinityPlate");
        p9.setDescription(
            "Nike React Infinity Run Flyknit 3 được nghiên cứu và thiết kế với mục tiêu giảm chấn thương " +
            "khi chạy, với tỷ lệ giảm chấn thương được chứng minh qua nghiên cứu lâm sàng. " +
            "Upper Flyknit mềm mại bao quanh bàn chân, hệ thống dây buộc phân tán lực đều trên toàn mu giày. " +
            "Tấm InfinityPlate nằm trong đế giữa giúp ổn định bước chân và phân phối đều lực đẩy. " +
            "Đệm React foam trả lại năng lượng hiệu quả, nhẹ hơn và bền hơn các thế hệ foam trước đó. " +
            "Lý tưởng cho người mới bắt đầu chạy bộ hoặc runner muốn bảo vệ đầu gối trong tập luyện dài hạn."
        );
        p9.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1556906781-9a412961d28c?w=600&q=80",
            "https://images.unsplash.com/photo-1578681994506-b8f463449011?w=600&q=80",
            "https://images.unsplash.com/photo-1552346154-21d32810aba3?w=600&q=80"
        ));
        p9.setSizes(Arrays.asList(
            ProductSize.builder().product(p9).sizeName("39").quantity(8).build(),
            ProductSize.builder().product(p9).sizeName("40").quantity(14).build(),
            ProductSize.builder().product(p9).sizeName("41").quantity(20).build(),
            ProductSize.builder().product(p9).sizeName("42").quantity(18).build(),
            ProductSize.builder().product(p9).sizeName("43").quantity(13).build(),
            ProductSize.builder().product(p9).sizeName("44").quantity(5).build()
        ));
        p9.setDeleted(false);

        // ---------- 10. Skechers D'Lites ----------
        Product p10 = new Product();
        p10.setName("Skechers D'Lites Memory Foam Nữ Hồng Trắng");
        p10.setPrice(1_990_000.0);
        p10.setType("Giày thể thao nữ");
        p10.setMaterial("Da tổng hợp, vải mesh, đế Memory Foam");
        p10.setDescription(
            "Skechers D'Lites là mẫu giày chunky dành cho nữ với phong cách những năm 90 trở lại mạnh mẽ. " +
            "Thiết kế đế dày nổi bật, upper phối màu hồng pastel và trắng ngọc lan nhẹ nhàng. " +
            "Bên trong lót Memory Foam Skechers ghi lại và thích nghi theo hình dạng bàn chân người dùng, " +
            "tạo cảm giác mang giày được đo riêng. " +
            "Đế ngoài cao su độ bền cao với họa tiết tạo ma sát tốt trên bề mặt trơn. " +
            "Rất phù hợp cho nữ yêu thích phong cách Y2K, thường ngày đi mua sắm, cà phê hay dạo phố."
        );
        p10.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1603787081207-362bcef7c144?w=600&q=80",
            "https://images.unsplash.com/photo-1613987245117-fc9f4f08a3f3?w=600&q=80",
            "https://images.unsplash.com/photo-1582588678413-dbf45f4823e9?w=600&q=80"
        ));
        p10.setSizes(Arrays.asList(
            ProductSize.builder().product(p10).sizeName("36").quantity(20).build(),
            ProductSize.builder().product(p10).sizeName("37").quantity(25).build(),
            ProductSize.builder().product(p10).sizeName("38").quantity(28).build(),
            ProductSize.builder().product(p10).sizeName("39").quantity(20).build(),
            ProductSize.builder().product(p10).sizeName("40").quantity(12).build(),
            ProductSize.builder().product(p10).sizeName("41").quantity(6).build()
        ));
        p10.setDeleted(false);

        // ---------- 11. Jordan 1 Retro High OG ----------
        Product p11 = new Product();
        p11.setName("Air Jordan 1 Retro High OG Chicago");
        p11.setPrice(6_200_000.0);
        p11.setType("Giày bóng rổ");
        p11.setMaterial("Da bò thuộc thượng hạng, đế Nike Air");
        p11.setDescription(
            "Air Jordan 1 Retro High OG Chicago là một trong những đôi giày được săn đón nhất mọi thời đại, " +
            "phục dựng trung thành đôi giày Michael Jordan mang trong mùa giải NBA 1984-1985. " +
            "Upper làm từ da bò thuộc thượng hạng với phối màu đỏ–trắng–đen iconic, " +
            "cổ giày cao bảo vệ mắt cá hiệu quả theo phong cách basketball cổ điển. " +
            "Đệm Air ở gót hấp thụ chấn động, đế cao su kẻ ô ngoài tăng độ bám sân. " +
            "Mỗi đôi đều đi kèm hộp cao cấp và đầy đủ phụ kiện dây giày dự phòng. " +
            "Không chỉ là giày — đây là một tác phẩm văn hóa pop đáng sưu tầm."
        );
        p11.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1597045566677-8cf032ed6634?w=600&q=80",
            "https://images.unsplash.com/photo-1612015398426-52e9f4a95893?w=600&q=80",
            "https://images.unsplash.com/photo-1600269452121-4f2416e55c28?w=600&q=80"
        ));
        p11.setSizes(Arrays.asList(
            ProductSize.builder().product(p11).sizeName("40").quantity(5).build(),
            ProductSize.builder().product(p11).sizeName("41").quantity(8).build(),
            ProductSize.builder().product(p11).sizeName("42").quantity(10).build(),
            ProductSize.builder().product(p11).sizeName("43").quantity(8).build(),
            ProductSize.builder().product(p11).sizeName("44").quantity(4).build()
        ));
        p11.setDeleted(false);

        // ---------- 12. Reebok Classic Leather ----------
        Product p12 = new Product();
        p12.setName("Reebok Classic Leather Trắng Kem Retro");
        p12.setPrice(2_100_000.0);
        p12.setType("Giày lifestyle");
        p12.setMaterial("Da bò mềm thuộc, lót EVA, đế DieGo cao su");
        p12.setDescription(
            "Reebok Classic Leather ra mắt năm 1983 và nhanh chóng trở thành đôi giày tennis đường phố " +
            "được ưa chuộng nhất thập kỷ. Ngày nay, mẫu giày này vẫn giữ nguyên vẹn DNA thiết kế tối giản, " +
            "sạch bóng của mình. " +
            "Upper da bò mềm thuộc tự nhiên theo thời gian sẽ nổi vân đẹp theo cách riêng của từng chủ nhân. " +
            "Lót EVA nhẹ, lớp lót vải terry thấm hút mồ hôi tốt. " +
            "Đế cao su DieGo bền bỉ với logo Union Jack trứ danh ở gót. " +
            "Màu trắng kem vintage tinh tế hơn trắng thuần, lý tưởng cho phong cách Old Money hay Quiet Luxury."
        );
        p12.setImages(Arrays.asList(
            "https://images.unsplash.com/photo-1519449556851-5720b33024e7?w=600&q=80",
            "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?w=600&q=80",
            "https://images.unsplash.com/photo-1517420704952-d9f39e95b43e?w=600&q=80"
        ));
        p12.setSizes(Arrays.asList(
            ProductSize.builder().product(p12).sizeName("38").quantity(12).build(),
            ProductSize.builder().product(p12).sizeName("39").quantity(18).build(),
            ProductSize.builder().product(p12).sizeName("40").quantity(24).build(),
            ProductSize.builder().product(p12).sizeName("41").quantity(22).build(),
            ProductSize.builder().product(p12).sizeName("42").quantity(15).build(),
            ProductSize.builder().product(p12).sizeName("43").quantity(8).build()
        ));
        p12.setDeleted(false);

        // ─── Lưu tất cả sản phẩm ───
        productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12));
        log.info("✅ Đã khởi tạo 12 sản phẩm giày thành công.");
    }
}
