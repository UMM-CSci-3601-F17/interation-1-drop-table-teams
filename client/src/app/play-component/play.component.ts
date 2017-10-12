import { Component, OnInit } from '@angular/core';
import {DeckService} from "../deck/deck.service";
import {ActivatedRoute} from "@angular/router";
import {Deck} from "../deck/deck";

@Component({
  selector: 'app-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.scss']
})
export class PlayComponent implements OnInit {

    deckid : string;

    deck : Deck;

    public pageNumber: number = 0;
    public pageCount: number = 0;


    constructor(public deckService : DeckService, private route: ActivatedRoute) {

    }


    ngOnInit() {
        this.route.params.subscribe(params => {
            this.deckid = params['deck'];

            this.deckService.getDeck(this.deckid).subscribe(
                deck => {
                    this.deck = deck;
                }
            );
        });
    }

}