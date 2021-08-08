package com.example.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchList {

    public static final WatchList EMPTY = new WatchList(new ArrayList<>());

    private List<Symbol> symbols = new ArrayList<>();
}
