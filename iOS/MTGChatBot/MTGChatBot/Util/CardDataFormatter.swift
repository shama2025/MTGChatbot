//
//  CardDataFormatter.swift
//  MTGChatBot
//
//  Created by Marcus Shaffer on 6/26/25.
//

import Foundation

class CardDataFormatter {
    func formatCardData(cardData: Card) -> String {
        let manaColor = formatCardManaColor(manaColor: cardData.colors)
        let legalities = formatLegalities(cardLegalities: cardData.legalities)

        let formattedString = (cardData.power == "0" && cardData.toughness == "0") || cardData.manaCost == "{0}" ?
            "The card \(cardData.name) has no power or toughness. It has no mana cost and is in the color identity of \(manaColor). \(cardData.name) has the ability \(cardData.oracleText ?? "no abilities"). \(legalities)" :
            "The card \(cardData.name) has a base power of \(cardData.power ?? "0") and a base toughness of \(cardData.toughness ?? "0"). It has a mana cost of \(cardData.manaCost ?? "no mana cost") and is in the color identity of \(manaColor). \(cardData.name) has the ability \(cardData.oracleText ?? "no abilities"). \(legalities)"
        print(formattedString)

        return formattedString
            .replacingOccurrences(of: "{T}", with: "Tap")
            .replacingOccurrences(of: "{G}", with: "Green ")
            .replacingOccurrences(of: "{B}", with: "Black ")
            .replacingOccurrences(of: "{R}", with: "Red ")
            .replacingOccurrences(of: "{W}", with: "White ")
            .replacingOccurrences(of: "{U}", with: "Blue ")
            .replacingOccurrences(of: "{C}", with: "Colorless ")
    }

    func formatCardManaColor(manaColor: [String]) -> String {
       // print("Formatting Mana Color Called\(manaColor)")
        if manaColor.isEmpty {
            return "Colorless"
        }

        var mutableManaColor = manaColor

        mutableManaColor = manaColor.map { "{\($0)}" }

        var output = ""
        for (index, color) in mutableManaColor.enumerated() {
            let isLast = index == manaColor.count - 1
            if isLast {
                output += color.uppercased()
            } else {
                output += "\(color), "
            }
        }

        return output
    }

    func handleCardRulesData(rulesData: CardRules) -> String {
        print("Rules Data: \(rulesData)")

        if rulesData.data.isEmpty {
            return "No rulings available"
        }

        var formattedString = ""

        for (_, ruling) in rulesData.data.enumerated() {
            formattedString += "\(ruling.comment)"
        }
        return formattedString
    }

    func handleCardSetData(setData: CardSet) -> String {
        let formattedString = "This card is from the set: \(setData.name)."

        return formattedString
    }
    
    func formatLegalities(cardLegalities: [String:String]) -> String{
        var res = "This card is allowed in: "

        let filteredKeys = cardLegalities.filter { $0.value == "legal" || $0.value == "restricted" }.map { $0.key }

        for (index, key) in filteredKeys.enumerated() {
            if index == filteredKeys.count - 1 {
                res += "and \(key)."
            } else {
                res += "\(key), "
            }
        }

        
        return res
    }
}
