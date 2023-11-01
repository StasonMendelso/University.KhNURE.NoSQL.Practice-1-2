package ua.nure.st.kpp.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.nure.st.kpp.example.demo.dao.CompanyDAO;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.IncomeJournalDAO;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.entity.Record;
import ua.nure.st.kpp.example.demo.form.journal.AddRecordForm;
import ua.nure.st.kpp.example.demo.form.journal.EditRecordForm;
import ua.nure.st.kpp.example.demo.service.TransformerService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/journal/income")
public class IncomeJournalController {
    private final IncomeJournalDAO incomeJournalDAO;
    private final CompanyDAO companyDAO;
    private final ItemDAO itemDAO;
    private final TransformerService transformerService;

    @Autowired
    public IncomeJournalController(IncomeJournalDAO incomeJournalDAO, CompanyDAO companyDAO, ItemDAO itemDAO, TransformerService transformerService) {
        this.incomeJournalDAO = incomeJournalDAO;
        this.companyDAO = companyDAO;
        this.itemDAO = itemDAO;
        this.transformerService = transformerService;
    }

    @GetMapping()
    public String showJournal(Model model) throws DAOException {
        model.addAttribute("incomeRecordsList", incomeJournalDAO.readAll().getRecords());
        return "journals/income/journal";
    }

    @GetMapping("/{id}")
    public String showRecord(Model model, @PathVariable("id") int id) throws DAOException {
        model.addAttribute("record", incomeJournalDAO.read(id));
        return "journals/showRecord";
    }

    @GetMapping("/add")
    public String newRecord(Model model, @ModelAttribute("addRecordForm") AddRecordForm addRecordForm) throws DAOException {
        addIdAttributes(model);
        return "journals/income/addRecord";
    }

    @PostMapping()
    public String createRecord(Model model, @ModelAttribute("addRecordForm") @Validated AddRecordForm addRecordForm,
                               BindingResult bindingResult) throws DAOException {
        if (bindingResult.hasErrors()) {
            addIdAttributes(model);
            return "journals/income/addRecord";
        }
        Record record = transformerService.toRecord(addRecordForm);
        record.setDate(LocalDateTime.now());
        incomeJournalDAO.createRecord(record);
        return "redirect:/journal/income";
    }

    @GetMapping("/{id}/edit")
    public String editRecord(Model model, @PathVariable("id") int id) throws DAOException {
        EditRecordForm editRecordForm = transformerService.toEditRecordForm(incomeJournalDAO.read(id));
        model.addAttribute("editRecordForm", editRecordForm);
        addIdAttributes(model);
        return "journals/income/editRecord";
    }


    @PatchMapping("/{id}")
    public String updateRecord(Model model, @ModelAttribute("editRecordForm") @Validated EditRecordForm editRecordForm,
                               BindingResult bindingResult, @PathVariable("id") int id) throws DAOException {
        if (bindingResult.hasErrors()) {
            addIdAttributes(model);
            return "journals/income/editRecord";
        }
        Record record = transformerService.toRecord(editRecordForm);
        incomeJournalDAO.updateRecord(id, record);
        return "redirect:/journal/income";
    }

    @DeleteMapping("/{id}")
    public String deleteRecord(@PathVariable("id") int id) throws DAOException {
        incomeJournalDAO.deleteRecord(id);
        return "redirect:/journal/income";
    }
    private void addIdAttributes(Model model) throws DAOException {
        model.addAttribute("itemIdList", itemDAO.readAllAvailableId());
        model.addAttribute("companyIdList", companyDAO.readAllAvailableId());
    }
}
