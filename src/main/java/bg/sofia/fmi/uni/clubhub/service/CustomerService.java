package bg.sofia.fmi.uni.clubhub.service;

import static bg.sofia.fmi.uni.clubhub.convertion.DataConverter.toEntity;
import static bg.sofia.fmi.uni.clubhub.convertion.DataConverter.toModel;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bg.sofia.fmi.uni.clubhub.convertion.DataConverter;
import bg.sofia.fmi.uni.clubhub.entity.CustomerEntity;
import bg.sofia.fmi.uni.clubhub.model.Customer;
import bg.sofia.fmi.uni.clubhub.repository.CustomerRepository;

@Service
public class CustomerService implements ICustomerService, UserDetailsService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<Customer> getById(UUID id) {
        return customerRepository.findById(id) //
                .map(DataConverter::toModel);
    }

    @Override
    public Optional<Customer> getByUsername(String username) {
        return customerRepository.findByUsername(username) //
                .map(DataConverter::toModel);
    }

    @Override
    public List<Customer> getAll(PageRequest page) {
        return customerRepository.findAll(page).stream() //
                .map(DataConverter::toModel) //
                .collect(toList());
    }

    @Override
    @Transactional
    public Customer createNew(Customer customer) {
        CustomerEntity entity = toEntity(customer);
        entity.setId(UUID.randomUUID());
        entity.setLeaderboardPoints(0);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));

        return toModel(customerRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        customerRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<CustomerEntity> user = customerRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException(format("User with username %s does not exist!", username));
        }

        return user.get();
    }
}
