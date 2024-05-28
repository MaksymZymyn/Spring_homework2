package homework2.resource;

import homework2.domain.bank.Account;
import homework2.domain.dto.account.AccountDtoResponse;
import homework2.exceptions.*;
import homework2.mapper.account.AccountDtoMapperResponse;
import homework2.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")         /* http://localhost:9000/accounts */
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class AccountRestController {

    private final AccountService accountService;
    private final AccountDtoMapperResponse accountDtoMapperResponse;

    @Operation(summary = "Get all accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all accounts",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDtoResponse.class))})
    })
    @GetMapping
    public List<AccountDtoResponse> getAll() {
        return accountService.findAll().stream().map(accountDtoMapperResponse::convertToDto).toList();
    }

    @Operation(summary = "Get an account by its number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the account",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDtoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content)
    })
    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> getByNumber(@PathVariable String accountNumber) {
        try {
            Account account = accountService.findByNumber(accountNumber);
            AccountDtoResponse accountDto = accountDtoMapperResponse.convertToDto(account);
            return ResponseEntity.ok(accountDto);
        } catch (IllegalArgumentException e) {
            log.error("Invalid account number format: {}", accountNumber, e);
            return ResponseEntity.badRequest().body("Invalid account number format: " + accountNumber);
        } catch (AccountNotFoundException e) {
            log.error("Account with number {} not found", accountNumber, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with number " + accountNumber + " not found");
        } catch (Exception e) {
            log.error("An unexpected error occurred while retrieving account with number {}", accountNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @Operation(summary = "Deposit an amount to an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDtoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content)
    })
    @PutMapping("/deposit/{accountNumber}")
    public ResponseEntity<?> deposit(@PathVariable String accountNumber, @RequestParam double amount) {
        try {
            if (amount <= 0) {
                log.error("Amount for deposit must be greater than 0");
                return ResponseEntity.badRequest().body("Amount for deposit must be greater than 0");
            }
            Account updatedAccount = accountService.deposit(accountNumber, amount);
            AccountDtoResponse dto = accountDtoMapperResponse.convertToDto(updatedAccount);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            log.error("Invalid account number format: {}", accountNumber, e);
            return ResponseEntity.badRequest().body("Invalid account number format: " + accountNumber);
        } catch (AccountNotFoundException e) {
            log.error("Account with number {} not found", accountNumber, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with number " + accountNumber + " not found");
        } catch (Exception e) {
            log.error("Unexpected error during deposit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @Operation(summary = "Withdraw an amount from an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDtoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content)
    })
    @PutMapping("/withdrawal/{accountNumber}")
    public ResponseEntity<?> withdraw(@PathVariable String accountNumber,
                                      @RequestParam double amount) {
        try {
            if (amount <= 0) {
                log.error("Amount for withdrawal must be greater than 0");
                return ResponseEntity.badRequest().body(Map.of("message", "Amount for withdrawal must be greater than 0"));
            }
            Account updatedAccount = accountService.withdraw(accountNumber, amount);
            AccountDtoResponse accountDto = accountDtoMapperResponse.convertToDto(updatedAccount);
            return ResponseEntity.ok(accountDto);
        } catch (IllegalArgumentException e) {
            log.error("Invalid account number format: {}", accountNumber, e);
            return ResponseEntity.badRequest().body("Invalid account number format: " + accountNumber);
        } catch (AccountNotFoundException e) {
            log.error("Account with number {} not found", accountNumber, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account with number " + accountNumber + " not found");
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during withdrawal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @Operation(summary = "Transfer an amount from one account to another")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDtoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content)
    })
    @PutMapping("/transfer/{fromAccountNumber}/{toAccountNumber}")
    public ResponseEntity<?> transfer(@PathVariable String fromAccountNumber,
                                      @PathVariable String toAccountNumber,
                                      @RequestParam double amount) {
        try {
            if (amount <= 0) {
                throw new InvalidTransferAmountException("Transfer amount must be greater than 0");
            }

            accountService.transfer(fromAccountNumber, toAccountNumber, amount);
            return ResponseEntity.ok("Transfer successful");
        } catch (IllegalArgumentException e) {
            log.error("Invalid account number format", e);
            return ResponseEntity.badRequest().body("Invalid account number format");
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SameAccountException | InvalidTransferAmountException | InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error transferring amount: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
