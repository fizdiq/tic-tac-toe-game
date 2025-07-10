package com.fizdiq.tictactoegame.viewmodels;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLayout;

public class MainLayout extends AppLayout implements RouterLayout {

    public MainLayout() {
        H1 title = new H1("Tic-Tac-Toe Game");
        title.getStyle().set("margin", "0 auto");

        HorizontalLayout header = new HorizontalLayout(title);
        header.setWidthFull();
        addToNavbar(header);
    }
}
