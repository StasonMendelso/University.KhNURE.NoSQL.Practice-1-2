package ua.nure.st.kpp.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.nure.st.kpp.example.demo.dao.CompanyDAO;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.entity.Company;
import ua.nure.st.kpp.example.demo.form.company.AddCompanyForm;
import ua.nure.st.kpp.example.demo.form.company.EditCompanyForm;
import ua.nure.st.kpp.example.demo.service.TransformerService;

@Controller
@RequestMapping("/companies")
public class CompaniesController {
    private final CompanyDAO companyDAO;
    private final TransformerService transformerService;

    @Autowired
    public CompaniesController(CompanyDAO companyDAO, TransformerService transformerService) {
        this.companyDAO = companyDAO;
        this.transformerService = transformerService;
    }

    @GetMapping()
    public String showAllCompanies(Model model) throws DAOException {
        model.addAttribute("companiesList", companyDAO.readAll());
        return "companies/companies";
    }

    @GetMapping("/add")
    public String newCompany(@ModelAttribute("addCompanyForm") AddCompanyForm addCompanyForm) {
        return "companies/addCompany";
    }

    @PostMapping()
    public String createCompany(@ModelAttribute("addCompanyForm") @Validated AddCompanyForm addCompanyForm,
                                BindingResult bindingResult) throws DAOException {
        if(bindingResult.hasErrors()){
            return "companies/addCompany";
        }
        Company company = transformerService.toCompany(addCompanyForm);
        companyDAO.create(company);
        return "redirect:/companies";
    }

    @GetMapping("/{id}/edit")
    public String editCompany(Model model, @PathVariable("id") String id) throws DAOException {
        EditCompanyForm editCompanyForm = transformerService.toEditCompanyForm(companyDAO.read(id));
        model.addAttribute("editCompanyForm",editCompanyForm);
        return "companies/editCompany";
    }

    @PatchMapping("/{id}")
    public String updateCompany(@ModelAttribute("editCompanyForm") @Validated EditCompanyForm editCompanyForm,
                                BindingResult bindingResult, @PathVariable("id") String id) throws DAOException {
        if(bindingResult.hasErrors()){
            return "companies/editCompany";
        }
        Company company = transformerService.toCompany(editCompanyForm);
        companyDAO.update(id,company);
        return "redirect:/companies";
    }

}
