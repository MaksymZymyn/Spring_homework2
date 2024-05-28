package homework2.domain.bank;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "address")
@ToString(exclude = "customers")
@NoArgsConstructor
@Entity
@Table(name = "employers")
@NamedEntityGraph(
        name = "employerWithCustomersAndAccountsAndOtherEmployers",
        attributeNodes = {
                @NamedAttributeNode(value = "customers", subgraph = "customerWithAccounts"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "customerWithAccounts",
                        attributeNodes = {
                                @NamedAttributeNode("accounts"),
                                @NamedAttributeNode("employers")
                        }
                )
        }
)
public class Employer extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false, unique = true)
    private String address;

    @ManyToMany(mappedBy = "employers", cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST}, fetch = FetchType.LAZY)
    private Set<Customer> customers = new HashSet<>();

    public Employer(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
