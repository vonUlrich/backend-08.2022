package ee.sten.webshop.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String image;
    private boolean active;
    //@Column(columnDefinition = "int(0) default 0")
    @ColumnDefault("0")
    private int stock;

    @ManyToOne
    private Category category;
}
