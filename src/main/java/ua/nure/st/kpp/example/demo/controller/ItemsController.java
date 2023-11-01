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
import org.springframework.web.bind.annotation.RequestParam;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.entity.Item;
import ua.nure.st.kpp.example.demo.form.item.EditItemForm;
import ua.nure.st.kpp.example.demo.service.TransformerService;
import ua.nure.st.kpp.example.demo.form.item.AddItemForm;


@Controller
@RequestMapping("/items")
public class ItemsController {
	private final ItemDAO itemDAO;
	private final TransformerService transformerService;

	@Autowired
	public ItemsController(ItemDAO itemDAO, TransformerService transformerService) {
		this.transformerService = transformerService;
		this.itemDAO = itemDAO;
	}

	@GetMapping()
	public String showAllItems(Model model) throws DAOException {
		model.addAttribute("itemsList", itemDAO.readAll());
		return "items/items";
	}

	@GetMapping("/search")
	public String showAllItemsByName(@RequestParam("name") String itemName,
									 Model model) throws DAOException {
		model.addAttribute("itemsList", itemDAO.readAllByName(itemName));
		return "items/items";
	}

	@GetMapping("/add")
	public String newItem(@ModelAttribute("addItemForm") AddItemForm addItemForm) {
		return "items/addItem";
	}

	@PostMapping()
	public String createItem(@ModelAttribute("addItemForm") @Validated AddItemForm addItemForm,
							 BindingResult bindingResult) throws DAOException {
		if (bindingResult.hasErrors()) {
			return "items/addItem";
		}
		Item item = transformerService.toItem(addItemForm);
		item.setAmount(0);
		itemDAO.create(item);
		return "redirect:/items";
	}

	@GetMapping("/{id}/edit")
	public String editItem(Model model, @PathVariable("id") int id) throws DAOException {
		EditItemForm editItemForm = transformerService.toEditItemForm(itemDAO.read(id));
		model.addAttribute("editItemForm", editItemForm);
		return "items/editItem";
	}

	@PatchMapping("/{id}")
	public String updateItem(@ModelAttribute("editItemForm") @Validated EditItemForm editItemForm, BindingResult bindingResult, @PathVariable("id") int id) throws DAOException {
		if (bindingResult.hasErrors()) {
			return "items/editItem";
		}
		Item item = transformerService.toItem(editItemForm);
		itemDAO.update(id, item);
		return "redirect:/items";
	}

	@DeleteMapping("/{id}")
	public String deleteItem(@PathVariable("id") int id) throws DAOException {
		itemDAO.delete(id);
		return "redirect:/items";
	}
}
