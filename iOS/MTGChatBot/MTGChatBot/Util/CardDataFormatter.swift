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

        let formattedString = (cardData.power == nil && cardData.toughness == nil) || cardData.manaCost == " " ?
            "The card \(cardData.name) has no power or toughness. It has no mana cost and is in the color identity of \(manaColor). \(cardData.name) has the ability \(String(describing: cardData.oracleText))." :
            "The card \(cardData.name) has a base power of \(cardData.power ?? "N/A") and a base toughness of \(cardData.toughness ?? "N/A"). It has a mana cost of \(String(describing: cardData.manaCost)) and is in the color identity of \(manaColor). \(cardData.name) has the ability \(String(describing: cardData.oracleText))."

        print(formattedString)

        return formattedString.replacingOccurrences(of: "{T}", with: "Tap")
    }

    func formatCardManaColor(manaColor: [String]) -> String {
        if manaColor.isEmpty {
            return "Colorless"
        }

        var output = ""
        for (index, color) in manaColor.enumerated() {
            let isLast = index == manaColor.count - 1
            if isLast {
                output += color.uppercased()
            } else {
                output += "\(color), "
            }
        }

        return output
            .replacingOccurrences(of: "{U}", with: "Blue ")
            .replacingOccurrences(of: "{G}", with: "Green ")
            .replacingOccurrences(of: "{B}", with: "Black ")
            .replacingOccurrences(of: "{R}", with: "Red ")
            .replacingOccurrences(of: "{W}", with: "White ")
            .replacingOccurrences(of: "{C}", with: "Colorless ")
    }

    func handleCardRulesData(rulesData: CardRules) -> String {
        print("Rules Data: \(rulesData)")

        if rulesData.data.isEmpty {
            return "No rulings available"
        }

        var formattedString = ""

        for (index, ruling) in rulesData.data.enumerated() {
            formattedString += "\(ruling.comment)"
        }
        return formattedString
    }

    func handleCardSetData(setData: CardSet) -> String {
        let formattedString = "This card is from the set: \(setData.name)."

        return formattedString
    }
}
