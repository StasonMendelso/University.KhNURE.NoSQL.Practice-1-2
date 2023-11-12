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
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.dao.OutcomeJournalDAO;
import ua.nure.st.kpp.example.demo.entity.Record;
import ua.nure.st.kpp.example.demo.form.journal.AddRecordForm;
import ua.nure.st.kpp.example.demo.form.journal.EditRecordForm;
import ua.nure.st.kpp.example.demo.service.TransformerService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/journal/outcome")
public class OutcomeJournalController {
    private final OutcomeJournalDAO outcomeJournalDAO;
    private final CompanyDAO companyDAO;
    private final ItemDAO itemDAO;
    private final TransformerService transformerService;

    @Autowired
    public OutcomeJournalController(OutcomeJournalDAO outcomeJournalDAO, CompanyDAO companyDAO, ItemDAO itemDAO, TransformerService transformerService) {
        this.outcomeJournalDAO = outcomeJournalDAO;
        this.companyDAO = companyDAO;
        this.itemDAO = itemDAO;
        this.transformerService = transformerService;
    }

    @GetMapping()
    public String showJournal(Model model) throws DAOException {
        model.addAttribute("outcomeRecordsList", outcomeJournalDAO.readAll().getRecords());
        return "journals/outcome/journal";
    }

    @GetMapping("/{id}")
    public String showRecord(Model model, @PathVariable("id") String id) throws DAOException {
        model.addAttribute("record", outcomeJournalDAO.read(id));
        return "journals/showRecord";
    }

    @GetMapping("/add")
    public String newRecord(Model model, @ModelAttribute("addRecordForm") AddRecordForm addRecordForm) throws DAOException {
        addIdAttributes(model);
        return "journals/outcome/addRecord";
    }

    @PostMapping()
    public String createRecord(Model model, @ModelAttribute("addRecordForm") @Validated AddRecordForm addRecordForm,
                               BindingResult bindingResult) throws DAOException {
        if (bindingResult.hasErrors()) {
            addIdAttributes(model);
            return "journals/outcome/addRecord";
        }
        Record record = transformerService.toRecord(addRecordForm);
        record.setDate(LocalDateTime.now());
        outcomeJournalDAO.createRecord(record);
        return "redirect:/journal/outcome";
    }


    @GetMapping("/{id}/edit")
    public String editRecord(Model model, @PathVariable("id") String id) throws DAOException {
        EditRecordForm editRecordForm = transformerService.toEditRecordForm(outcomeJournalDAO.read(id));
        model.addAttribute("editRecordForm", editRecordForm);
        addIdAttributes(model);
        return "journals/outcome/editRecord";
    }

    @PatchMapping("/{id}")
    public String updateRecord(Model model, @ModelAttribute("editRecordForm") @Validated EditRecordForm editRecordForm,
                               BindingResult bindingResult, @PathVariable("id") String id) throws DAOException {
        if (bindingResult.hasErrors()) {
            addIdAttributes(model);
            return "journals/outcome/editRecord";
        }
        Record record = transformerService.toRecord(editRecordForm);
        outcomeJournalDAO.updateRecord(id, record);
        return "redirect:/journal/outcome";
    }

    @DeleteMapping("/{id}")
    public String deleteRecord(@PathVariable("id") String id) throws DAOException {
        outcomeJournalDAO.deleteRecord(id);
        return "redirect:/journal/outcome";
    }

    private void addIdAttributes(Model model) throws DAOException {
        model.addAttribute("itemIdList", itemDAO.readAllAvailableId());
        model.addAttribute("companyIdList", companyDAO.readAllAvailableId());
    }

}
